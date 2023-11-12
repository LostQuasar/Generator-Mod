package net.furia.generator.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.furia.generator.GeneratorMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GeneratorScreen extends HandledScreen<GeneratorScreenHandler> {
    // texture
    private static final Identifier TEXTURE = new Identifier(GeneratorMod.MOD_ID, "textures/gui/container/generator.png");

    public GeneratorScreen(GeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // progress %
        int progress = handler.getScaledPercentage();
        Text progressText = Text.of(progress + "%");
        // center align text
        int textWidth = textRenderer.getWidth(progressText);
        int textX = x + (backgroundWidth - textWidth) / 2;
        int textY = y + 40;

        context.drawText(textRenderer, progress + "%", textX, textY, 0x3F3F3F, false);

        // write if disabled
        if (handler.isDisabled()) {
            Text disabledText = Text.of("Disabled");
            int disabledTextWidth = textRenderer.getWidth(disabledText);
            int disabledTextX = x + (backgroundWidth - disabledTextWidth) / 2;
            int disabledTextY = y + 16;
            context.drawText(textRenderer, disabledText, disabledTextX, disabledTextY, 0x3F3F3F, false);
        }

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
