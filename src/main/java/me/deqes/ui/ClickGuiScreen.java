package me.deqes.ui;

import com.google.common.eventbus.Subscribe;
import me.deqes.Laguna;
import me.deqes.event.*;
import me.deqes.module.Category;
import me.deqes.module.Module;
import me.deqes.ui.element.CategoryElement;
import me.deqes.ui.element.ModuleElement;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.deqes.util.Wrapper.mc;

public class ClickGuiScreen extends Screen {

    private int panelX, panelY;
    private int panelWidth = 420;
    private int panelHeight = 320;

    private int dragX, dragY;
    private boolean dragging = false;

    private List<CategoryElement> categoryElements = new ArrayList<>();
    private List<ModuleElement> moduleElements = new ArrayList<>();

    private int scrollOffset = 0;
    private int maxScroll = 0;

    public ClickGuiScreen() {
        super(Text.of("ClickGui"));

        panelX = (mc.getWindow().getScaledWidth() - panelWidth) / 2;
        panelY = (mc.getWindow().getScaledHeight() - panelHeight) / 2;

        for (Category category : Category.values()) {
            categoryElements.add(new CategoryElement(category));
        }
        for (Module module : Laguna.getInstance().getModuleManager().getModules()) {
            moduleElements.add(new ModuleElement(module));
        }
        Laguna.getInstance().getEventBus().register(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        RenderUtil.drawRect(context, 0, 0, mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(), new Color(0, 0, 0, 150));

        // Рисуем тень (простая, без RenderSystem)
        for (int i = 8; i >= 1; i--) {
            RenderUtil.drawRect(context, panelX - i, panelY - i, panelWidth + i * 2, panelHeight + i * 2,
                    new Color(0, 0, 0, 15));
        }

        // Основная панель
        RenderUtil.drawRect(context, panelX, panelY, panelWidth, panelHeight, new Color(20, 20, 25, 245));

        // Рамка
        RenderUtil.drawRect(context, panelX, panelY, panelWidth, 2, new Color(80, 180, 80, 200)); // Верхняя зеленая полоска
        RenderUtil.drawRect(context, panelX, panelY + panelHeight - 1, panelWidth, 1, new Color(60, 60, 70));
        RenderUtil.drawRect(context, panelX, panelY, 1, panelHeight, new Color(60, 60, 70));
        RenderUtil.drawRect(context, panelX + panelWidth - 1, panelY, 1, panelHeight, new Color(60, 60, 70));

        // Заголовок
        RenderUtil.drawRect(context, panelX, panelY + 2, panelWidth, 32, new Color(30, 30, 38, 255));

        // Название
        String title = "LAGUNA CLIENT";
        int titleWidth = mc.textRenderer.getWidth(title);
        RenderUtil.drawText(context, panelX + panelWidth/2 - titleWidth/2, panelY + 12, title,
                new Color(255, 255, 255));

        // Декоративная линия под названием
        RenderUtil.drawRect(context, panelX + panelWidth/2 - 35, panelY + 28, 70, 2,
                new Color(80, 180, 80));

        // Drag зона (точечки)
        for (int i = 0; i < 3; i++) {
            RenderUtil.drawRect(context, panelX + 12 + i * 4, panelY + 16, 2, 2, new Color(150, 150, 150));
            RenderUtil.drawRect(context, panelX + 12 + i * 4, panelY + 22, 2, 2, new Color(150, 150, 150));
        }

        // Drag & Drop
        if (dragging) {
            panelX = mouseX - dragX;
            panelY = mouseY - dragY;

            // Ограничения
            panelX = Math.max(0, Math.min(panelX, mc.getWindow().getScaledWidth() - panelWidth));
            panelY = Math.max(0, Math.min(panelY, mc.getWindow().getScaledHeight() - panelHeight));
        }

        // Рендер категорий
        int categoryX = panelX + 10;
        int categoryY = panelY + 45;

        for (CategoryElement categoryElement : categoryElements) {
            categoryElement.setX(categoryX);
            categoryElement.setY(categoryY);
            categoryElement.setWidth(95);
            categoryElement.setHeight(28);
            categoryElement.render(context);
            categoryY += 34;
        }

        // Рендер модулей (если выбрана категория)
        if (selectedCategory != null) {
            renderModules(context);
        }
    }

    private void renderModules(DrawContext context) {
        // Собираем модули выбранной категории
        List<ModuleElement> visibleModules = new ArrayList<>();
        for (ModuleElement moduleElement : moduleElements) {
            if (moduleElement.getModule().getCategory() == selectedCategory.getCategory()) {
                visibleModules.add(moduleElement);
            }
        }

        // 2 колонки
        int moduleWidth = 120;
        int moduleHeight = 26;
        int startX = panelX + 125;
        int startY = panelY + 45;

        // Вычисляем максимальный скролл
        int rows = (int) Math.ceil(visibleModules.size() / 2.0);
        int totalHeight = rows * (moduleHeight + 5);
        int viewHeight = 230;
        maxScroll = Math.max(0, totalHeight - viewHeight);

        // Скролл бар
        if (maxScroll > 0) {
            int barHeight = (int)(viewHeight * (viewHeight / (float)totalHeight));
            barHeight = Math.max(30, Math.min(barHeight, viewHeight - 10));
            int barY = startY + (int)((scrollOffset / (float)maxScroll) * (viewHeight - barHeight));

            // Фон скролл бара
            RenderUtil.drawRect(context, panelX + panelWidth - 12, startY, 4, viewHeight,
                    new Color(40, 40, 50, 150));
            // Ползунок
            RenderUtil.drawRect(context, panelX + panelWidth - 12, barY, 4, barHeight,
                    new Color(80, 180, 80, 200));
        }

        // Рендер модулей
        int index = 0;
        for (ModuleElement module : visibleModules) {
            int column = index % 2;
            int row = index / 2;

            int x = startX + (column * (moduleWidth + 10));
            int y = startY + (row * (moduleHeight + 5)) - scrollOffset;

            // Проверяем, виден ли модуль
            if (y + moduleHeight > startY && y < startY + viewHeight) {
                module.setX(x);
                module.setY(y);
                module.setWidth(moduleWidth);
                module.setHeight(moduleHeight);
                module.render(context);
            }

            index++;
        }
    }

    @Subscribe
    public void onMouse(EventMouse e) {
        if (e.getAction() != 1) return;

        int mouseX = (int)e.getMouseX();
        int mouseY = (int)e.getMouseY();

        // Проверка на drag зону
        if (mouseX >= panelX && mouseX <= panelX + panelWidth &&
                mouseY >= panelY && mouseY <= panelY + 34) {
            dragging = true;
            dragX = mouseX - panelX;
            dragY = mouseY - panelY;
            return;
        }

        // Клик по категориям
        for (CategoryElement categoryElement : categoryElements) {
            categoryElement.mouseClicked(mouseX, mouseY);
        }

        // Клик по модулям
        if (selectedCategory != null) {
            for (ModuleElement moduleElement : moduleElements) {
                if (moduleElement.getModule().getCategory() == selectedCategory.getCategory()) {
                    moduleElement.mouseClicked(mouseX, mouseY, e.getKey());
                }
            }
        }
    }

    @Subscribe
    public void onMouseRelease(EventMouse e) {
        if (e.getAction() == 0) {
            dragging = false;
        }
    }

    @Subscribe
    public void onKey(EventKey e) {
        if (e.getAction() != 1) return;

        // Обработка биндинга
        for (ModuleElement moduleElement : moduleElements) {
            if (moduleElement.isBinding()) {
                if (e.getKey() == 256) { // ESC отменяет биндинг
                    moduleElement.setBinding(false);
                } else {
                    moduleElement.keyPressed(e.getKey());
                }
                return;
            }
        }

        // Закрытие по ESC
        if (e.getKey() == 256) {
            mc.setScreen(null);
        }
    }

    @Subscribe
    public void onScroll(EventMouseScroll e) {
        if (selectedCategory != null) {
            scrollOffset -= e.getScroll() * 15;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        }
    }

    @Override
    public void removed() {
        Laguna.getInstance().getEventBus().unregister(this);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static CategoryElement selectedCategory = null;
}