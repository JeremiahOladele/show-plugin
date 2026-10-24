
package net.storryteller.showPlugin.internal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static net.storryteller.showPlugin.ShowPlugin.ANSI_RESET;
import static net.storryteller.showPlugin.ShowPlugin.ANSI_YELLOW;

public final class AnimatedStructure {

    private static final int ANIMATION_TICKS = 10;
    private static final int BLOCK_INTERVAL_TICKS = 4;
    private final Plugin plugin;

    public AnimatedStructure(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    public BuildSession buildAnimatedStructure(File structureFile, Location origin) throws IOException {
        Objects.requireNonNull(structureFile, "Structure file cannot be null");
        Objects.requireNonNull(origin, "Origin cannot be null");
        World world = Objects.requireNonNull(origin.getWorld(), "Origin world cannot be null");

        if (!structureFile.isFile()) {
            System.out.println(ANSI_RESET + ANSI_YELLOW +
                    "\n " +
                    "Structure file : '" + structureFile.getAbsolutePath() + "' does not exist" + ANSI_RESET);
        }

        Structure structure = plugin.getServer().getStructureManager().loadStructure(structureFile);
        List<PlacedBlock> blocks = readBlocks(structure);
        int originX = origin.getBlockX();
        int originY = origin.getBlockY();
        int originZ = origin.getBlockZ();

        BuildSession session = new BuildSession(world, originX, originY, originZ, blocks);
        session.start();
        return session;
    }

    private List<PlacedBlock> readBlocks(Structure structure) {
        List<Palette> palettes = structure.getPalettes();

        if (palettes.isEmpty()) {throw new IllegalArgumentException("The structure has no block palettes.");}

        Palette palette = palettes.get(0);
        List<PlacedBlock> blocks = new ArrayList<>();

        for (BlockState state : palette.getBlocks()) {
            Material material = state.getType();
            if (material.isAir()) {continue;}

            blocks.add(new PlacedBlock(
                    state.copy(),
                    state.getX(),
                    state.getY(),
                    state.getZ()
            ));
        }

        blocks.sort(Comparator.comparingInt((PlacedBlock b) -> b.y)
                        .thenComparingInt(b -> b.x)
                        .thenComparingInt(b -> b.z));
        return blocks;
    }

    private static final class PlacedBlock {
        private final BlockState state;
        private final int x;
        private final int y;
        private final int z;

        private PlacedBlock(BlockState state, int x, int y, int z) {
            this.state = state;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public final class BuildSession {
        private final World world;
        private final int originX;
        private final int originY;
        private final int originZ;
        private final List<PlacedBlock> blocks;
        private final List<ActiveDisplay> activeDisplays = new ArrayList<>();

        private int nextBlockIndex = 0;
        private long tick = 0;
        private long nextSpawnTick = 1;

        private BukkitTask task;
        private boolean finished = false;
        private boolean cancelled = false;

        private BuildSession(World world, int originX, int originY, int originZ, List<PlacedBlock> blocks) {
            this.world = world;
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
            this.blocks = blocks;
        }

        private void start() {
            if (blocks.isEmpty()) {
                finished = true;
                return;
            }

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        tickBuild();
                    } catch (RuntimeException exception) {
                        System.out.println(ANSI_RESET + ANSI_YELLOW +
                                "\n " +
                                "Animated structure failed: " + exception.getMessage() + ANSI_RESET);
                        exception.printStackTrace();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);
        }

        private void tickBuild() {
            tick++;

            if (nextBlockIndex < blocks.size() && tick >= nextSpawnTick) {
                spawnBlock(blocks.get(nextBlockIndex));

                nextBlockIndex++;
                nextSpawnTick += BLOCK_INTERVAL_TICKS;
            }

            var iterator = activeDisplays.iterator();
            while (iterator.hasNext()) {
                ActiveDisplay active = iterator.next();

                if (!active.growing && tick >= active.growAt) {
                    beginGrowth(active.display);
                    active.growing = true;
                }
                if (tick >= active.finishAt) {
                    replaceWithFinalBlock(active);
                    iterator.remove();
                }
            }
            if (nextBlockIndex >= blocks.size() && activeDisplays.isEmpty()) {
                finished = true;
                task.cancel();
            }
        }

        private void spawnBlock(PlacedBlock block) {
            Location location = new Location(
                    world,
                    originX + block.x,
                    originY + block.y,
                    originZ + block.z
            );

            BlockDisplay display = world.spawn(
                    location,
                    BlockDisplay.class,
                    entity -> {
                        entity.setBlock(block.state.getBlockData());
                        entity.setPersistent(false);
                        entity.setGravity(false);
                        entity.setTransformation(transformation(0.0f));
                        entity.setInterpolationDelay(0);
                        entity.setInterpolationDuration(ANIMATION_TICKS);
                    }
            );
            long growAt = tick + 1;
            long finishAt = growAt + ANIMATION_TICKS;

            activeDisplays.add(new ActiveDisplay(block, location, display, growAt, finishAt));
        }

        private void beginGrowth(BlockDisplay display) {
            if (!display.isValid()) {return;}
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(ANIMATION_TICKS);
            display.setTransformation(transformation(1.0f));
        }

        private Transformation transformation(float scale) {
            float offset = (1.0f - scale) / 2.0f;
            return new Transformation(
                    new Vector3f(offset, offset, offset),
                    new AxisAngle4f(),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f()
            );
        }

        private void replaceWithFinalBlock(ActiveDisplay active) {
            BlockDisplay display = active.display;
            PlacedBlock savedBlock = active.block;

            Location targetLocation = active.location;
            Block target = world.getBlockAt(
                    targetLocation.getBlockX(),
                    targetLocation.getBlockY(),
                    targetLocation.getBlockZ()
            );

            try {
                BlockState placedState = savedBlock.state.copy(targetLocation);
                if (!placedState.update(true, false)) {
                    target.setBlockData(savedBlock.state.getBlockData(), false);
                }
            } finally {
                if (display.isValid()) {display.remove();}
            }
        }
    }

    private static final class ActiveDisplay {
        private final PlacedBlock block;
        private final Location location;
        private final BlockDisplay display;
        private final long growAt;
        private final long finishAt;
        private boolean growing = false;

        private ActiveDisplay(PlacedBlock block, Location location, BlockDisplay display, long growAt, long finishAt) {
            this.block = block;
            this.location = location;
            this.display = display;
            this.growAt = growAt;
            this.finishAt = finishAt;
        }
    }
}
