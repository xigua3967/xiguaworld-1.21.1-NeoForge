package com.xigua.xiguaworld.client;

import com.xigua.xiguaworld.player.ModPlayerEnergy;
import com.xigua.xiguaworld.player.ModPlayerGrade;
import com.xigua.xiguaworld.player.ModPlayerSwordGrade;
import com.xigua.xiguaworld.player.ModPlayermagicGrade;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

/**
 * 技能系统 GUI 界面
 * 继承自 Minecraft 的 Screen 类，用于显示玩家的技能信息
 * 
 * 布局说明：
 * ┌──────────────────────────────────────────────────────────────────┐
 * │  左侧主要内容区                              │  右侧标签按钮区      │
 * │  ┌────────────────────────────────────┐     │  ┌────┐            │
 * │  │  个体名：XXX    种族：人类           │     │  │ 0  │  ← 个体信息  │
 * │  │  ───────────────────────────      │     │  └────┘            │
 * │  │                                     │     │  ┌────┐            │
 * │  │  体术属性标题                        │     │  │ 1  │  ← 体术      │
 * │  │  肌力：---  耐力：XX  敏捷：---  灵巧：---│     │  └────            │
 * │  │  ────────────────────────────      │     │  ────┐            │
 * │  │  等级信息标题                        │     │  │ 2  │  ← 魔术      │
 * │  │  综合等级：XX                        │     │  └────┘            │
 * │  │  等级细分：                          │     │  ────┐            │
 * │  │  剑术等级：XX  魔术等级：XX           │     │  │ 3  │  ← 加护      │
 * │  │                                     │     │  └────┘            │
 * │  └────────────────────────────────────┘     │                    │
 * └──────────────────────────────────────────────────────────────────┘
 * 
 * 贴图说明：
 * - GUI 背景贴图：textures/gui/skill_system.png
 * - 标签按钮贴图：textures/gui/tab_button.png（需包含 4 种状态）
 * 
 * 使用方法：
 * 1. 通过按键绑定（默认 G 键）打开此 GUI
 * 2. 点击右侧标签按钮切换不同页面
 * 3. 按 ESC 或 E 键关闭 GUI
 */
@OnlyIn(Dist.CLIENT)
public class ModSkillSystemGui extends Screen {

    /**
     * GUI 背景纹理资源路径
     * 用于绘制 GUI 主背景图片
     * 纹理文件应放置在：src/main/resources/assets/xigua_world/textures/gui/skill_system.png
     * 
     * 使用方法：
     * guiGraphics.blit(GUI_TEXTURE, x, y, u, v, width, height, texWidth, texHeight);
     */
    private static final ResourceLocation GUI_TEXTURE = 
            ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "textures/gui/skill_system.png");

    /**
     * 标签按钮纹理资源路径
     * 用于绘制右侧标签按钮
     * 纹理文件应放置在：src/main/resources/assets/xigua_world/textures/gui/tab_button.png
     * 
     * 纹理布局建议（以 40x40 按钮为例）：
     * ┌────────────────┬────────┬────────┐
     * │ 默认   │ 悬停   │ 选中   │ 禁用   │
     * │ (0,0)  │ (40,0) │ (80,0) │ (120,0)│
     * └────────┴────────┴────────┴────────┘
     */
    private static final ResourceLocation TAB_BUTTON_TEXTURE = 
            ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "textures/gui/tab_button.png");

    /**
     * 标签页翻译键数组
     * 对应语言文件中的翻译键，用于显示标签按钮文字
     * 索引 0: 个体信息（显示所有三栏内容）
     * 索引 1: 体术（占位符）
     * 索引 2: 魔术（占位符）
     * 索引 3: 加护（占位符）
     */
    private static final String[] TABS = {
            "gui.xigua_world.skill.tab.info",
            "gui.xigua_world.skill.tab.body",
            "gui.xigua_world.skill.tab.magic",
            "gui.xigua_world.skill.tab.blessing"
    };

    /** 当前选中的标签页索引（0-3） */
    private int currentTab = 0;
    
    /** 当前玩家实例，用于获取玩家数据 */
    private LocalPlayer player;

    /** GUI 左上角 X 坐标（屏幕坐标） */
    private int guiLeft;
    
    /** GUI 左上角 Y 坐标（屏幕坐标） */
    private int guiTop;
    
    /** GUI 总宽度（像素） */
    private final int guiWidth = 420;
    
    /** GUI 总高度（像素） */
    private final int guiHeight = 260;
    
    /** 右侧标签按钮区域宽度（像素） */
    private final int tabAreaWidth = 60;
    
    /** 左侧主要内容区域宽度（像素） */
    private final int contentAreaWidth = guiWidth - tabAreaWidth - 10;

    /**
     * 构造函数
     * 调用父类 Screen 的构造函数，设置 GUI 标题
     * 标题通过翻译键 "gui.xigua_world.skill.title" 获取
     */
    public ModSkillSystemGui() {
        super(Component.translatable("gui.xigua_world.skill.title"));
    }

    /**
     * 初始化方法
     * 在 GUI 创建时调用，用于初始化玩家实例和计算 GUI 位置
     * 
     * 作用：
     * 1. 获取当前玩家实例
     * 2. 计算 GUI 在屏幕中央的位置
     * 
     * 注意：此方法在每次打开 GUI 时都会调用
     */
    @Override
    protected void init() {
        super.init();
        this.player = Minecraft.getInstance().player;
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;
    }

    /**
     * 渲染方法 - GUI 的主渲染入口
     * 每帧调用一次，负责绘制整个 GUI 界面
     * 
     * @param guiGraphics GUI 图形绘制工具，用于绘制矩形、文字、纹理等
     * @param mouseX 鼠标 X 坐标（用于悬停效果）
     * @param mouseY 鼠标 Y 坐标（用于悬停效果）
     * @param partialTick 部分刻时间（用于平滑动画）
     * 
     * 渲染顺序：
     * 1. 渲染背景（半透明黑色遮罩）
     * 2. 绘制 GUI 背景贴图
     * 3. 绘制左侧内容区域
     * 4. 绘制右侧标签按钮
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        drawGuiBackground(guiGraphics);
        drawContentArea(guiGraphics, mouseX, mouseY);
        drawTabButtons(guiGraphics, mouseX, mouseY);
    }

    /**
     * 绘制 GUI 背景贴图
     * 使用您自己绘制的纹理图片作为 GUI 背景
     * 
     * 贴图文件位置：
     * src/main/resources/assets/xigua_world/textures/gui/skill_system.png
     * 
     * 贴图规格建议：
     * - 尺寸：420x260 像素（与 GUI 尺寸一致）
     * - 格式：PNG（支持透明）
     * - 内容：包含外边框、分隔线、背景色等所有视觉元素
     * 
     * 如果您还没有准备好贴图，可以暂时使用纯色填充作为占位
     */
    private void drawGuiBackground(GuiGraphics guiGraphics) {
        // 使用纹理贴图绘制 GUI 背景
        guiGraphics.blit(
            GUI_TEXTURE,      // 纹理资源
            guiLeft,          // 屏幕 X 坐标
            guiTop,           // 屏幕 Y 坐标
            0,                // 纹理 U 坐标（起始 X）
            0,                // 纹理 V 坐标（起始 Y）
            guiWidth,         // 绘制宽度
            guiHeight,        // 绘制高度
            guiWidth,         // 纹理总宽度
            guiHeight         // 纹理总高度
        );
    }

    /**
     * 绘制左侧内容区域
     * 显示当前标签页的具体内容
     * 
     * 布局说明：
     * - 内容区域占据 GUI 左侧大部分空间
     * - 信息横向排列，节省垂直空间
     * - 右侧为独立的标签按钮区域
     * 
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     */
    private void drawContentArea(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 计算内容面板边界
        int contentLeft = guiLeft + 10;
        int contentTop = guiTop + 10;
        int contentRight = guiLeft + contentAreaWidth - 10;
        int contentBottom = guiTop + guiHeight - 10;

        // 根据当前标签页索引渲染对应内容
        switch (currentTab) {
            case 0:
                // 个体信息标签页：显示所有三栏内容（横向排列）
                renderInfoTab(guiGraphics, contentLeft, contentTop, contentRight, contentBottom);
                break;
            case 1:
                // 体术标签页：占位符
                renderPlaceholderTab(guiGraphics, contentLeft, contentTop, "gui.xigua_world.skill.tab.body");
                break;
            case 2:
                // 魔术标签页：占位符
                renderPlaceholderTab(guiGraphics, contentLeft, contentTop, "gui.xigua_world.skill.tab.magic");
                break;
            case 3:
                // 加护标签页：占位符
                renderPlaceholderTab(guiGraphics, contentLeft, contentTop, "gui.xigua_world.skill.tab.blessing");
                break;
        }
    }

    /**
     * 绘制右侧标签按钮
     * 在 GUI 右侧绘制四个标签切换按钮
     * 
     * @param mouseX 鼠标 X 坐标（用于检测悬停）
     * @param mouseY 鼠标 Y 坐标（用于检测悬停）
     * 
     * 按钮状态：
     * - 默认状态：使用纹理的默认部分
     * - 悬停状态：使用纹理的悬停部分
     * - 选中状态：使用纹理的选中部分
     * 
     * 按钮布局：
     * - 宽度：40 像素
     * - 高度：40 像素
     * - 间距：8 像素（等距离间距）
     * - 位置：GUI 右侧区域，垂直居中
     * 
     * 贴图文件位置：
     * src/main/resources/assets/xigua_world/textures/gui/tab_button.png
     */
    private void drawTabButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int tabButtonWidth = 40;
        int tabButtonHeight = 40;
        int tabButtonGap = 8;  // 等距离间距
        
        // 计算按钮区域起始位置（右侧区域居中）
        int tabAreaLeft = guiLeft + contentAreaWidth + 10;
        int totalButtonsHeight = TABS.length * tabButtonHeight + (TABS.length - 1) * tabButtonGap;
        int tabStartY = guiTop + (guiHeight - totalButtonsHeight) / 2;

        for (int i = 0; i < TABS.length; i++) {
            int buttonX = tabAreaLeft + (tabAreaWidth - tabButtonWidth) / 2;
            int buttonY = tabStartY + i * (tabButtonHeight + tabButtonGap);

            // 检测按钮状态
            boolean isSelected = (i == currentTab);
            boolean isHovered = mouseX >= buttonX && mouseX <= buttonX + tabButtonWidth &&
                    mouseY >= buttonY && mouseY <= buttonY + tabButtonHeight;

            // 使用纹理贴图绘制按钮
            int textureU = i * 40;  // 每个按钮对应贴图的不同区域
            guiGraphics.blit(
                TAB_BUTTON_TEXTURE,
                buttonX, buttonY,
                textureU, 0,        // 纹理坐标（根据按钮索引选择）
                tabButtonWidth, tabButtonHeight,
                160, 40             // 纹理总尺寸（4 个按钮 x 40 像素）
            );

            // 鼠标悬停时，在按钮右侧显示文字
            if (isHovered) {
                Component tabName = Component.translatable(TABS[i]);
                int textX = buttonX + tabButtonWidth + 10;  // 按钮右侧 10 像素
                int textY = buttonY + tabButtonHeight / 2 - 4;  // 垂直居中
                int textColor = 0xFFB0C8E0;

                guiGraphics.drawString(this.font, tabName, textX, textY, textColor);
            }
        }
    }

    /**
     * 渲染个体信息标签页内容
     * 显示所有三栏内容（横向排列）：
     * 第一栏：个体名（玩家名字）、种族（人类）- 同一行
     * 第二栏：肌力、耐力、敏捷、灵巧 - 同一行横向排列
     * 第三栏：综合等级、等级细分（剑术等级、魔术等级，为0不显示）
     * 
     * @param guiGraphics GUI 图形绘制工具
     * @param left 内容区域左边界
     * @param top 内容区域上边界
     * @param right 内容区域右边界
     * @param bottom 内容区域下边界
     */
    private void renderInfoTab(GuiGraphics guiGraphics, int left, int top, int right, int bottom) {
        int y = top + 3;  // 下降 5 像素
        int lineHeight = 22;

        if (player != null) {
            // ========== 第一栏：个体信息（横向排列） ==========
            // 显示个体名
            Component nameLabel = Component.translatable("gui.xigua_world.skill.name");
            Component nameValue = Component.literal(player.getGameProfile().getName());
            guiGraphics.drawString(this.font, nameLabel, left, y, 0xFFB0C8E0);
            guiGraphics.drawString(this.font, nameValue, left + this.font.width(nameLabel) + 10, y, 0xFFFFFF);

            // 显示种族（与个体名同一行）
            int raceX = left + 180;  // 种族起始 X 坐标
            Component raceLabel = Component.translatable("gui.xigua_world.skill.race");
            Component raceValue = Component.translatable("gui.xigua_world.skill.race.human");
            guiGraphics.drawString(this.font, raceLabel, raceX, y, 0xFFB0C8E0);
            guiGraphics.drawString(this.font, raceValue, raceX + this.font.width(raceLabel) + 10, y, 0xFFFFFF);
            y += lineHeight + 4;  // 第一栏与第二栏间距

            // ========== 第二栏：体术属性（横向排列） ==========
            // 横向排列四个属性
            int statSpacing = 80;  // 属性之间的间距
            drawStatRow(guiGraphics, left, y, "gui.xigua_world.skill.body.strength", "---");
            drawStatRow(guiGraphics, left + statSpacing, y, "gui.xigua_world.skill.body.endurance", getEnduranceDisplay());
            drawStatRow(guiGraphics, left + statSpacing * 2, y, "gui.xigua_world.skill.body.agility", "---");
            drawStatRow(guiGraphics, left + statSpacing * 3, y, "gui.xigua_world.skill.body.dexterity", "---");
            y += lineHeight + 4;  // 第二栏与第三栏间距

            // ========== 第三栏：等级信息 ==========
            // 显示综合等级
            ModPlayerGrade.Grade grade = player.getCapability(ModPlayerGrade.ENTITY, null);
            int currentGrade = grade != null ? grade.getCurrentGrade() : 0;
            drawStatRow(guiGraphics, left, y, "gui.xigua_world.skill.magic.total_grade", String.valueOf(currentGrade));
            y += lineHeight + 1;

            // 显示等级细分标题
            Component subLabel = Component.translatable("gui.xigua_world.skill.magic.sub_grade");
            guiGraphics.drawString(this.font, subLabel, left, y, 0xFFB0C8E0);
            y += lineHeight;

            // 显示剑术等级（仅当等级 > 0 时显示）
            ModPlayerSwordGrade.SwordGrade swordGrade = player.getCapability(ModPlayerSwordGrade.ENTITY, null);
            int swordGradeValue = swordGrade != null ? swordGrade.getCurrentSwordGrade() : 0;
            if (swordGradeValue > 0) {
                drawStatRow(guiGraphics, left, y, "gui.xigua_world.skill.magic.sword_grade", String.valueOf(swordGradeValue));
                
                // 魔术等级与剑术等级同一行显示
                ModPlayermagicGrade.MagicGrade magicGrade = player.getCapability(ModPlayermagicGrade.ENTITY, null);
                int magicGradeValue = magicGrade != null ? magicGrade.getCurrentMagicGrade() : 0;
                if (magicGradeValue > 0) {
                    drawStatRow(guiGraphics, left + 150, y, "gui.xigua_world.skill.magic.magic_grade", String.valueOf(magicGradeValue));
                }
            } else {
                // 剑术等级为 0 时，单独显示魔术等级
                ModPlayermagicGrade.MagicGrade magicGrade = player.getCapability(ModPlayermagicGrade.ENTITY, null);
                int magicGradeValue = magicGrade != null ? magicGrade.getCurrentMagicGrade() : 0;
                if (magicGradeValue > 0) {
                    drawStatRow(guiGraphics, left, y, "gui.xigua_world.skill.magic.magic_grade", String.valueOf(magicGradeValue));
                }
            }
        }
    }

    /**
     * 渲染占位符标签页内容
     * 用于体术、魔术、加护等暂未实现的标签页
     * 
     * @param guiGraphics GUI 图形绘制工具
     * @param left 内容区域左边界
     * @param top 内容区域上边界
     * @param titleKey 标题翻译键
     */
    private void renderPlaceholderTab(GuiGraphics guiGraphics, int left, int top, String titleKey) {
        Component title = Component.translatable(titleKey);
        Component placeholder = Component.translatable("gui.xigua_world.skill.placeholder");
        
        guiGraphics.drawString(this.font, title, left, top, 0xFF00DDFF);
        guiGraphics.drawString(this.font, placeholder, left, top + 25, 0xFF8090A0);
    }

    /**
     * 绘制属性行
     * 通用的属性显示方法，用于显示标签和对应的数值
     * 
     * @param guiGraphics GUI 图形绘制工具
     * @param left 行左边界
     * @param y 行 Y 坐标
     * @param labelKey 标签翻译键（从语言文件获取显示文本）
     * @param value 属性值（直接显示的字符串）
     * 
     * 使用方法：
     * drawStatRow(guiGraphics, x, y, "gui.xigua_world.skill.body.strength", "100");
     * 将显示：肌力：100
     */
    private void drawStatRow(GuiGraphics guiGraphics, int left, int y, String labelKey, String value) {
        Component label = Component.translatable(labelKey);
        guiGraphics.drawString(this.font, label, left, y, 0xFFB0C8E0);

        int labelWidth = this.font.width(label);
        guiGraphics.drawString(this.font, Component.literal(value), left + labelWidth + 5, y, 0xFFFFFF);
    }

    /**
     * 获取耐力显示文本
     * 从玩家能力系统获取当前耐力和最大耐力值
     * 
     * @return 格式为 "当前值/最大值" 的字符串，如果获取失败返回 "---"
     * 
     * 使用方法：
     * String display = getEnduranceDisplay();
     * // 返回示例："150/200"
     */
    private String getEnduranceDisplay() {
        if (player != null) {
            ModPlayerEnergy.Endurance endurance = player.getCapability(ModPlayerEnergy.ENTITY, null);
            if (endurance != null) {
                return String.valueOf((int) endurance.getMaxEndurance()/16);
            }
        }
        return "---";
    }

    /**
     * 鼠标点击事件处理
     * 检测是否点击了标签按钮，如果是则切换标签页
     * 
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param button 鼠标按钮（0=左键，1=右键，2=中键）
     * @return 如果点击了标签按钮返回 true，否则返回父类处理结果
     * 
     * 工作原理：
     * 1. 计算每个标签按钮的位置
     * 2. 检测鼠标是否在按钮范围内
     * 3. 如果在范围内，更新 currentTab 并返回 true
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int tabButtonWidth = 40;
        int tabButtonHeight = 40;
        int tabButtonGap = 8;
        
        int tabAreaLeft = guiLeft + contentAreaWidth + 10;
        int totalButtonsHeight = TABS.length * tabButtonHeight + (TABS.length - 1) * tabButtonGap;
        int tabStartY = guiTop + (guiHeight - totalButtonsHeight) / 2;

        for (int i = 0; i < TABS.length; i++) {
            int buttonX = tabAreaLeft + (tabAreaWidth - tabButtonWidth) / 2;
            int buttonY = tabStartY + i * (tabButtonHeight + tabButtonGap);

            if (mouseX >= buttonX && mouseX <= buttonX + tabButtonWidth &&
                    mouseY >= buttonY && mouseY <= buttonY + tabButtonHeight) {
                currentTab = i;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 键盘按键事件处理
     * 处理 ESC 和 E 键关闭 GUI
     * 
     * @param keyCode 按键代码（GLFW 键码）
     * @param scanCode 扫描码
     * @param modifiers 修饰键状态（Shift、Ctrl 等）
     * @return 如果处理了按键返回 true，否则返回父类处理结果
     * 
     * 支持的按键：
     * - ESC：关闭 GUI
     * - E：关闭 GUI（与物品栏按键相同）
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * 判断是否为暂停屏幕
     * 返回 false 表示打开此 GUI 时游戏不会暂停
     * 
     * @return false - 游戏不暂停
     * 
     * 注意：
     * - 返回 true：打开 GUI 时游戏暂停（如暂停菜单）
     * - 返回 false：打开 GUI 时游戏继续运行（如物品栏、聊天框）
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}