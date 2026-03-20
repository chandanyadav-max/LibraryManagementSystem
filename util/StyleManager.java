package util;

import javafx.scene.Scene;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StyleManager {

    private static String cssUri;

    private static final String CSS = """
        /* ─── Base ──────────────────────────────────────────────── */
        .root {
            -fx-font-family: "Segoe UI", "SF Pro Text", "Ubuntu", Arial, sans-serif;
            -fx-font-size: 13px;
        }

        /* ─── Login ─────────────────────────────────────────────── */
        .login-root {
            -fx-background-color: #1A2744;
        }
        .login-card {
            -fx-background-color: white;
            -fx-background-radius: 14px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 30, 0, 0, 10);
            -fx-padding: 40 48 40 48;
            -fx-min-width: 420px;
            -fx-max-width: 420px;
        }
        .login-title {
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-text-fill: #1A2744;
            -fx-text-alignment: center;
            -fx-wrap-text: true;
        }
        .login-subtitle {
            -fx-font-size: 12px;
            -fx-text-fill: #64748B;
        }
        .field-label {
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            -fx-text-fill: #374151;
        }
        .login-error {
            -fx-text-fill: #EF4444;
            -fx-font-size: 12px;
        }
        .login-version {
            -fx-font-size: 11px;
            -fx-text-fill: #94A3B8;
        }

        /* ─── Sidebar ────────────────────────────────────────────── */
        .sidebar {
            -fx-background-color: #1E2D5A;
            -fx-min-width: 240px;
            -fx-max-width: 240px;
            -fx-pref-width: 240px;
        }
        .sidebar-header {
            -fx-background-color: #16224A;
            -fx-padding: 20 18 20 18;
        }
        .sidebar-title {
            -fx-text-fill: white;
            -fx-font-size: 15px;
            -fx-font-weight: bold;
            -fx-wrap-text: true;
        }
        .sidebar-subtitle {
            -fx-text-fill: #8DA4C4;
            -fx-font-size: 11px;
        }
        .sidebar-divider {
            -fx-background-color: #2A3F6E;
            -fx-min-height: 1px;
            -fx-max-height: 1px;
            -fx-pref-height: 1px;
        }
        .nav-section-label {
            -fx-text-fill: #5A7BAA;
            -fx-font-size: 10px;
            -fx-font-weight: bold;
            -fx-padding: 14 18 4 18;
        }
        .nav-btn {
            -fx-background-color: transparent;
            -fx-text-fill: #94A3B8;
            -fx-font-size: 13px;
            -fx-alignment: CENTER-LEFT;
            -fx-padding: 11 18 11 18;
            -fx-pref-width: 240px;
            -fx-max-width: Infinity;
            -fx-background-radius: 0;
            -fx-cursor: hand;
            -fx-graphic-text-gap: 10px;
        }
        .nav-btn:hover {
            -fx-background-color: #2A3F6E;
            -fx-text-fill: #E2E8F0;
        }
        .nav-btn-active {
            -fx-background-color: #2E75B6;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        }
        .nav-btn-active:hover {
            -fx-background-color: #2565A6;
        }
        .sidebar-footer {
            -fx-background-color: #16224A;
            -fx-padding: 14 18 14 18;
            -fx-border-color: #2A3F6E transparent transparent transparent;
            -fx-border-width: 1 0 0 0;
        }
        .user-name  { -fx-text-fill: white;   -fx-font-size: 13px; -fx-font-weight: bold; }
        .user-role  { -fx-text-fill: #8DA4C4; -fx-font-size: 11px; }
        .avatar-circle {
            -fx-background-color: #2E75B6;
            -fx-background-radius: 20px;
            -fx-min-width: 36px;  -fx-max-width: 36px;
            -fx-min-height: 36px; -fx-max-height: 36px;
        }
        .avatar-initials {
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
        }
        .logout-btn {
            -fx-background-color: transparent;
            -fx-text-fill: #EF4444;
            -fx-border-color: #EF4444;
            -fx-border-radius: 6px;
            -fx-background-radius: 6px;
            -fx-padding: 6 14;
            -fx-font-size: 12px;
            -fx-cursor: hand;
            -fx-pref-width: 200px;
        }
        .logout-btn:hover { -fx-background-color: rgba(239,68,68,0.08); }

        /* ─── Content Area ───────────────────────────────────────── */
        .content-area { -fx-background-color: #F0F4F8; }
        .content-header {
            -fx-background-color: white;
            -fx-padding: 14 28;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);
        }
        .page-title {
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: #1E2D5A;
        }
        .page-subtitle {
            -fx-font-size: 12px;
            -fx-text-fill: #64748B;
        }
        .content-scroll { -fx-background-color: transparent; }
        .content-scroll .viewport { -fx-background-color: transparent; }

        /* ─── Stat Cards ─────────────────────────────────────────── */
        .stat-card {
            -fx-background-color: white;
            -fx-background-radius: 12px;
            -fx-padding: 22;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 2);
            -fx-min-width: 200px;
        }
        .stat-icon-box {
            -fx-background-radius: 10px;
            -fx-min-width: 44px;  -fx-max-width: 44px;
            -fx-min-height: 44px; -fx-max-height: 44px;
            -fx-alignment: center;
        }
        .stat-number {
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: #1E2D5A;
        }
        .stat-label { -fx-font-size: 12px; -fx-text-fill: #64748B; }
        .stat-blue  .stat-icon-box { -fx-background-color: #DBEAFE; }
        .stat-green .stat-icon-box { -fx-background-color: #D1FAE5; }
        .stat-amber .stat-icon-box { -fx-background-color: #FEF3C7; }
        .stat-red   .stat-icon-box { -fx-background-color: #FEE2E2; }
        .stat-blue  .stat-number   { -fx-text-fill: #1D4ED8; }
        .stat-green .stat-number   { -fx-text-fill: #065F46; }
        .stat-amber .stat-number   { -fx-text-fill: #92400E; }
        .stat-red   .stat-number   { -fx-text-fill: #991B1B; }

        /* ─── Buttons ────────────────────────────────────────────── */
        .primary-btn {
            -fx-background-color: #2E75B6;
            -fx-text-fill: white;
            -fx-background-radius: 7px;
            -fx-padding: 8 18;
            -fx-font-size: 12px;
            -fx-cursor: hand;
        }
        .primary-btn:hover { -fx-background-color: #1F5A96; }
        .danger-btn {
            -fx-background-color: #EF4444;
            -fx-text-fill: white;
            -fx-background-radius: 7px;
            -fx-padding: 8 18;
            -fx-font-size: 12px;
            -fx-cursor: hand;
        }
        .danger-btn:hover { -fx-background-color: #DC2626; }
        .success-btn {
            -fx-background-color: #10B981;
            -fx-text-fill: white;
            -fx-background-radius: 7px;
            -fx-padding: 8 18;
            -fx-font-size: 12px;
            -fx-cursor: hand;
        }
        .success-btn:hover { -fx-background-color: #059669; }
        .secondary-btn {
            -fx-background-color: #E2E8F0;
            -fx-text-fill: #374151;
            -fx-background-radius: 7px;
            -fx-padding: 8 18;
            -fx-font-size: 12px;
            -fx-cursor: hand;
        }
        .secondary-btn:hover { -fx-background-color: #CBD5E1; }
        .warning-btn {
            -fx-background-color: #F59E0B;
            -fx-text-fill: white;
            -fx-background-radius: 7px;
            -fx-padding: 8 18;
            -fx-font-size: 12px;
            -fx-cursor: hand;
        }
        .warning-btn:hover { -fx-background-color: #D97706; }

        /* ─── Tables ─────────────────────────────────────────────── */
        .table-view {
            -fx-background-color: white;
            -fx-border-color: #E2E8F0;
            -fx-border-radius: 10px;
            -fx-background-radius: 10px;
        }
        .table-view .column-header-background {
            -fx-background-color: #F8FAFC;
            -fx-background-radius: 10px 10px 0 0;
        }
        .table-view .column-header {
            -fx-background-color: transparent;
            -fx-border-color: transparent transparent #E2E8F0 transparent;
            -fx-border-width: 0 0 1 0;
        }
        .table-view .column-header .label {
            -fx-text-fill: #374151;
            -fx-font-weight: bold;
            -fx-font-size: 12px;
        }
        .table-row-cell:even  { -fx-background-color: white; }
        .table-row-cell:odd   { -fx-background-color: #F8FAFC; }
        .table-row-cell:hover { -fx-background-color: #EFF6FF; }
        .table-row-cell:selected { -fx-background-color: #DBEAFE; }
        .table-row-cell:selected .text { -fx-fill: #1E2D5A; }
        .table-view .placeholder .label { -fx-text-fill: #94A3B8; }

        /* ─── Badges ─────────────────────────────────────────────── */
        .badge {
            -fx-padding: 3 10;
            -fx-background-radius: 20px;
            -fx-font-size: 11px;
            -fx-font-weight: bold;
        }
        .badge-available { -fx-background-color: #D1FAE5; -fx-text-fill: #065F46; }
        .badge-issued    { -fx-background-color: #FEF3C7; -fx-text-fill: #92400E; }
        .badge-overdue   { -fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; }
        .badge-returned  { -fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; }
        .badge-reserved  { -fx-background-color: #F3E8FF; -fx-text-fill: #6B21A8; }
        .badge-paid      { -fx-background-color: #D1FAE5; -fx-text-fill: #065F46; }
        .badge-unpaid    { -fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; }

        /* ─── Form Fields ────────────────────────────────────────── */
        .text-field, .password-field, .text-area {
            -fx-background-color: white;
            -fx-background-radius: 7px;
            -fx-border-radius: 7px;
            -fx-border-color: #CBD5E1;
            -fx-border-width: 1.5;
            -fx-padding: 8 12;
            -fx-font-size: 13px;
        }
        .text-field:focused, .password-field:focused, .text-area:focused {
            -fx-border-color: #2E75B6;
            -fx-border-width: 2;
        }
        .combo-box {
            -fx-background-color: white;
            -fx-background-radius: 7px;
            -fx-border-radius: 7px;
            -fx-border-color: #CBD5E1;
            -fx-border-width: 1.5;
        }
        .combo-box:focused { -fx-border-color: #2E75B6; }
        .combo-box .list-cell { -fx-padding: 6 12; -fx-font-size: 13px; }
        .spinner .text-field { -fx-border-radius: 7px 0 0 7px; }

        /* ─── Section / Panel ────────────────────────────────────── */
        .panel-card {
            -fx-background-color: white;
            -fx-background-radius: 10px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);
            -fx-padding: 0;
        }
        .section-title {
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-text-fill: #1E2D5A;
        }
        .search-box {
            -fx-background-radius: 20px;
            -fx-border-radius: 20px;
            -fx-pref-width: 240px;
        }

        /* ─── Dialog ─────────────────────────────────────────────── */
        .dialog-root {
            -fx-background-color: #F0F4F8;
        }
        .dialog-header {
            -fx-background-color: #1E2D5A;
            -fx-padding: 16 24;
        }
        .dialog-title {
            -fx-text-fill: white;
            -fx-font-size: 15px;
            -fx-font-weight: bold;
        }
        .dialog-body {
            -fx-background-color: white;
            -fx-padding: 24;
        }
        .dialog-footer {
            -fx-background-color: #F8FAFC;
            -fx-padding: 14 24;
            -fx-border-color: #E2E8F0 transparent transparent transparent;
            -fx-border-width: 1 0 0 0;
        }
        .form-label {
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            -fx-text-fill: #374151;
        }

        /* ─── Fine / Alert ───────────────────────────────────────── */
        .fine-box {
            -fx-background-color: #FEF3C7;
            -fx-background-radius: 8px;
            -fx-padding: 12 16;
            -fx-border-color: #F59E0B;
            -fx-border-width: 1;
            -fx-border-radius: 8px;
        }
        .fine-text-warn { -fx-text-fill: #92400E; -fx-font-weight: bold; }
        .no-fine-box {
            -fx-background-color: #D1FAE5;
            -fx-background-radius: 8px;
            -fx-padding: 12 16;
            -fx-border-color: #10B981;
            -fx-border-width: 1;
            -fx-border-radius: 8px;
        }
        .no-fine-text { -fx-text-fill: #065F46; -fx-font-weight: bold; }

        /* ─── Misc ───────────────────────────────────────────────── */
        .separator .line { -fx-border-color: transparent transparent #E2E8F0 transparent; }
        .scroll-bar { -fx-background-color: transparent; }
        .scroll-bar .thumb { -fx-background-color: #CBD5E1; -fx-background-radius: 4px; }
        .scroll-bar .track { -fx-background-color: transparent; }
        """;

    public static void apply(Scene scene) {
        try {
            if (cssUri == null) {
                Path tmp = Files.createTempFile("lms_styles_", ".css");
                Files.writeString(tmp, CSS);
                tmp.toFile().deleteOnExit();
                cssUri = tmp.toUri().toString();
            }
            scene.getStylesheets().add(cssUri);
        } catch (IOException e) {
            System.err.println("CSS load failed: " + e.getMessage());
        }
    }
}