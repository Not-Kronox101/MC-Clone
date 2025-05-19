package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import player.Player;
import utils.RayCaster;
import world.Block;
import world.World;
import render.Renderer;
import utils.RayCaster.RaycastResult;
import world.MinecraftClone;

public class InputHandler {
    private final Player player;
    private final World world;
    private final Renderer renderer;
    private final MinecraftClone game;
    private float moveSpeed = 0.2f;
    private boolean inventoryOpen = false;

    public InputHandler(Player player, World world, Renderer renderer, MinecraftClone game) {
        this.player = player;
        this.world = world;
        this.renderer = renderer;
        this.game = game;
        // Initialize cursor state
        Gdx.input.setCursorCatched(true);
    }

    public void update() {
        handleInventoryToggle();
        if (!inventoryOpen) {
            handleMovement();
            handleBlockInteraction();
        }
        handleInventoryInput();
    }

    public boolean isInventoryOpen() {
        return inventoryOpen;
    }

    private void handleInventoryToggle() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            inventoryOpen = !inventoryOpen;
            renderer.setInventoryVisible(inventoryOpen);
            Gdx.input.setCursorCatched(!inventoryOpen);
        }
    }

    private void handleMovement() {
        float yawRad = (float) Math.toRadians(player.getYaw());

        // Forward vector (direction player is facing)
        float forwardX = (float) Math.sin(yawRad);
        float forwardZ = (float) Math.cos(yawRad);

        // Strafe vector (perpendicular to forward)
        float strafeX = (float) Math.cos(yawRad);
        float strafeZ = (float) -Math.sin(yawRad);

        float dx = 0, dy = 0, dz = 0;

        // W - Forward
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dx += forwardX * moveSpeed;
            dz += forwardZ * moveSpeed;
        }
        // S - Backward
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dx -= forwardX * moveSpeed;
            dz -= forwardZ * moveSpeed;
        }
        // A - Strafe Left
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx += strafeX * moveSpeed;
            dz += strafeZ * moveSpeed;
        }
        // D - Strafe Right
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx -= strafeX * moveSpeed;
            dz -= strafeZ * moveSpeed;
        }
        // SPACE - Fly Up
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            dy += moveSpeed;
        }
        // Z - Fly Down
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            dy -= moveSpeed;
        }

        // Move with Minecraft-style collision
        player.moveWithCollision(dx, dy, dz, world);
    }

    private void handleBlockInteraction() {
        RaycastResult hit = RayCaster.cast(world, player);
        if (hit == null) return;

        // Break block with L key
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            Block block = world.getBlock(hit.x, hit.y, hit.z);
            if (block != Block.AIR) {
                // Remove block and add to inventory
                world.setBlock(hit.x, hit.y, hit.z, Block.AIR);
                player.addToInventory(block);
                // Update renderer
                renderer.updateBlock(hit.x, hit.y, hit.z, Block.AIR);
            }
        }

        // Place block with K key
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            // Calculate placement position based on hit face
            int placeX = hit.x + hit.faceX;
            int placeY = hit.y + hit.faceY;
            int placeZ = hit.z + hit.faceZ;

            // Check if placement position is valid
            if (world.isInBounds(placeX, placeY, placeZ)) {
                Block currentBlock = world.getBlock(placeX, placeY, placeZ);
                if (currentBlock == Block.AIR) {
                    // Check if player has the block in inventory
                    Block blockToPlace = player.getSelectedBlock();
                    if (blockToPlace != null) {
                        // Check if placement would cause collision with player
                        if (!player.wouldCollide(placeX + 0.5f, placeY + 0.5f, placeZ + 0.5f)) {
                            world.setBlock(placeX, placeY, placeZ, blockToPlace);
                            player.removeFromInventory(blockToPlace);
                            // Update renderer
                            renderer.updateBlock(placeX, placeY, placeZ, blockToPlace);
                        }
                    }
                }
            }
        }
    }

    private void handleInventoryInput() {
        if (inventoryOpen) {
            handleInventoryMouseInput();
        } else {
            // Number keys 1-9 for hotbar selection
            for (int i = 0; i < 9; i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                    player.getInventory().setSelectedSlot(i);
                }
            }

            // Mouse wheel for hotbar scrolling
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.getInventory().selectNextSlot();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                player.getInventory().selectPreviousSlot();
            }
        }

        // Q to drop item (works in both inventory and hotbar)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Block selectedBlock = player.getInventory().getSelectedBlock();
            if (selectedBlock != Block.AIR) {
                player.getInventory().removeItem(selectedBlock, 1);
                // TODO: Implement item dropping in the world
            }
        }
    }

    private void handleInventoryMouseInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int slot = getSlotUnderMouse();
            if (slot >= 0 && slot < player.getInventory().TOTAL_SIZE) {
                // Swap with selected slot
                int selectedSlot = player.getInventory().getSelectedSlot();
                player.getInventory().swapSlots(slot, selectedSlot);
            }
        }
    }

    private int getSlotUnderMouse() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        float slotSize = 40f;
        float inventoryWidth = slotSize * 9;
        float inventoryHeight = slotSize * 4;
        float startX = (Gdx.graphics.getWidth() - inventoryWidth) / 2;
        float startY = (Gdx.graphics.getHeight() - inventoryHeight) / 2;

        if (mouseX >= startX && mouseX < startX + inventoryWidth &&
            mouseY >= startY && mouseY < startY + inventoryHeight) {
            int col = (int)((mouseX - startX) / slotSize);
            int row = (int)((mouseY - startY) / slotSize);
            return row * 9 + col;
        }
        return -1;
    }
}
