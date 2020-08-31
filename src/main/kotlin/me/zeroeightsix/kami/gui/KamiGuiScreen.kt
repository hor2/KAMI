package me.zeroeightsix.kami.gui

import glm_.vec4.Vec4
import imgui.Col
import imgui.ConfigFlag
import imgui.ImGui
import imgui.impl.glfw.ImplGlfw
import imgui.wo
import me.zeroeightsix.kami.feature.hidden.PrepHandler
import me.zeroeightsix.kami.gui.widgets.EnabledWidgets
import me.zeroeightsix.kami.gui.windows.Settings
import me.zeroeightsix.kami.gui.windows.modules.Modules
import me.zeroeightsix.kami.gui.wizard.Wizard
import me.zeroeightsix.kami.util.Texts.lit
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

object KamiGuiScreen : Screen(lit("Kami GUI") as Text?) {

    val rainbowForeground = listOf(
        Col.Text.i,
        Col.TextDisabled.i,
        Col.WindowBg.i,
        Col.ChildBg.i,
        Col.PopupBg.i,
        Col.Border.i,
        Col.BorderShadow.i,
        Col.FrameBg.i,
        Col.FrameBgHovered.i,
        Col.FrameBgActive.i,
        Col.TitleBg.i,
        Col.TitleBgActive.i,
        Col.TitleBgCollapsed.i,
        Col.MenuBarBg.i,
        Col.ScrollbarBg.i,
        Col.ScrollbarGrab.i,
        Col.ScrollbarGrabHovered.i,
        Col.ScrollbarGrabActive.i,
        Col.CheckMark.i,
        Col.SliderGrab.i,
        Col.SliderGrabActive.i,
        Col.Button.i,
        Col.ButtonHovered.i,
        Col.ButtonActive.i,
        Col.Header.i,
        Col.HeaderHovered.i,
        Col.HeaderActive.i,
        Col.Separator.i,
        Col.SeparatorHovered.i,
        Col.SeparatorActive.i,
        Col.ResizeGrip.i,
        Col.ResizeGripHovered.i,
        Col.ResizeGripActive.i,
        Col.Tab.i,
        Col.TabHovered.i,
        Col.TabActive.i,
        Col.TabUnfocused.i,
        Col.TabUnfocusedActive.i,
        Col.PlotLines.i,
        Col.PlotLinesHovered.i,
        Col.PlotHistogram.i,
        Col.PlotHistogramHovered.i,
        Col.TextSelectedBg.i,
        Col.DragDropTarget.i,
        Col.NavHighlight.i,
        Col.NavWindowingHighlight.i,
        Col.NavWindowingDimBg.i,
        Col.ModalWindowDimBg.i
    )

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val returned = super.keyPressed(keyCode, scanCode, modifiers)
        if (!returned) {
            ImplGlfw.keyCallback(keyCode, scanCode, GLFW.GLFW_PRESS, modifiers)
        }
        return returned
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val returned = super.keyReleased(keyCode, scanCode, modifiers)
        if (!returned) {
            ImplGlfw.keyCallback(keyCode, scanCode, GLFW.GLFW_RELEASE, modifiers)
        }
        return returned
    }

    override fun charTyped(chr: Char, keyCode: Int): Boolean {
        val returned = super.charTyped(chr, keyCode)
        if (!returned) {
            ImplGlfw.charCallback(chr.toInt())
        }
        return returned
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        KamiHud.frame(matrices!!) {
            if (Wizard()) return@frame;

            this()
        }
    }

    operator fun invoke() {
        if (Settings.rainbowMode) {
            Themes.Variants.values()[Settings.styleIdx].applyStyle()
            val colors = ImGui.style.colors
            rainbowForeground.forEach { idx ->
                val col = colors[idx]
                val buf = FloatArray(3)
                ImGui.colorConvertRGBtoHSV(col.toVec3().toFloatArray(), buf)
                buf[0] = PrepHandler.getRainbowHue(buf[0].toDouble()).toFloat()
                ImGui.colorConvertHSVtoRGB(buf, buf)
                colors[idx] = Vec4(buf[0], buf[1], buf[2], col[3])
            }
        }

        // Draw the main menu bar.
        MenuBar()
        // Debug window (theme, demo window)
        if (View.demoWindowVisible) {
            ImGui.showDemoWindow(View::demoWindowVisible)
        }
        // Draw all module windows
        Modules()
        // Draw the settings
        Settings()

        if (!EnabledWidgets.hideAll) {
            showWidgets()
        }
    }

    fun showWidgets(limitY: Boolean = true) {
        val iterator = EnabledWidgets.widgets.iterator()
        for (widget in iterator) {
            if (widget.open && widget.showWindow(limitY)) {
                iterator.remove()
            }
        }
    }

    override fun onClose() {
        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NoMouse.i
        super.onClose()
    }

    override fun init() {
        super.init()
        ImGui.io.configFlags = ImGui.io.configFlags wo ConfigFlag.NoMouse.i
    }

}
