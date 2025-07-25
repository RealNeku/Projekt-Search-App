package group6.java.ui;

import java.awt.Dimension;
import java.awt.Insets;

/**
 * 桌面端网页布局常量 (基于现代网页设计标准适配Swing)
 */
public class LayoutConstants {
    // ======================== 容器尺寸 ========================
    /** 主窗口尺寸 */
    public static final Dimension FRAME_SIZE = new Dimension(1440, 900);

    /** 内容区域最大宽度 (参考Bootstrap XL容器) */
    public static final int CONTENT_MAX_WIDTH = 1320;

    /** 左右侧边栏宽度 */
    public static final int SIDEBAR_WIDTH = FRAME_SIZE.width / 3;

    /** 顶部导航栏高度 */
    public static final int TOP_BAR_HEIGHT = 80;

    // ======================== 间距系统 (基于8px基准) =============
    /** 基础间距单位 */
    public static final int BASE_SPACING = 8;

    /** 微型间距 (4px) */
    public static final int SPACE_XS = BASE_SPACING / 2;

    /** 小间距 (8px) */
    public static final int SPACE_SM = BASE_SPACING;

    /** 中间距 (16px) */
    public static final int SPACE_MD = BASE_SPACING * 2;

    /** 大间距 (24px) */
    public static final int SPACE_LG = BASE_SPACING * 3;

    /** 超大间距 (32px) */
    public static final int SPACE_XL = BASE_SPACING * 4;

    /** 巨型间距 (48px) */
    public static final int SPACE_XXL = BASE_SPACING * 6;

    // ======================== 边距设置 ========================
    /** 页面内容安全边距 */
    public static final Insets PAGE_PADDING = new Insets(SPACE_XL, SPACE_XXL, SPACE_XL, SPACE_XXL);

    /** 卡片内部边距 */
    public static final Insets CARD_PADDING = new Insets(SPACE_MD, SPACE_MD, SPACE_MD, SPACE_MD);

    /** 表单元素间距 */
    public static final int FORM_ITEM_GAP = SPACE_MD;

    // ======================== 圆角大小 ========================
    /** 小圆角 (按钮/输入框) */
    public static final int RADIUS_SMALL = 4;

    /** 中圆角 (卡片) */
    public static final int RADIUS_MEDIUM = 8;

    /** 大圆角 (特殊形状) */
    public static final int RADIUS_LARGE = 12;

    // ======================== 组件尺寸 ========================
    /** 按钮尺寸 */
    public static final Dimension BUTTON_SIZE = new Dimension(120, 40);

    /** 输入框高度 */
    public static final int INPUT_HEIGHT = 36;


    /** 结果卡片尺寸 */
    public static final Dimension CARD_SIZE = new Dimension(
            (FRAME_SIZE.width - SIDEBAR_WIDTH) - 100,  // 右侧可用宽度-边距
            180
    );

    /** 图标尺寸 */
    public static final Dimension ICON_SIZE = new Dimension(24, 24);

    // ======================== 阴影参数 ========================
    /** 卡片阴影偏移量 */
    public static final int SHADOW_OFFSET = 2;

    /** 阴影模糊半径 */
    public static final int SHADOW_BLUR = 8;

    // ======================== 字体大小 ========================
    /** 基础字体大小 (相当于网页的1rem) */
    public static final int FONT_BASE = 14;

    /** 小号文字 (0.875rem) */
    public static final int FONT_SMALL = 12;

    /** 大号文字 (1.25rem) */
    public static final int FONT_LARGE = 18;

    /** 标题大小 (h4) */
    public static final int FONT_H4 = 20;

    /** 标题大小 (h3) */
    public static final int FONT_H3 = 24;

    /** 标题大小 (h2) */
    public static final int FONT_H2 = 32;

    /** 标题大小 (h1) */
    public static final int FONT_H1 = 48;
}