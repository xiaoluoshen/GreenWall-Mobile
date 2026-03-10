# GreenWall Mobile - Design Document (MIUIX Style)

## Design Language

This app follows the **MIUIX** design language — a clean, modern, rounded UI style inspired by Xiaomi's HyperOS/MIUI. Key principles: large rounded corners, generous whitespace, theme-colored interactive elements, and clear information hierarchy.

## Screen List

| Screen | Tab | Purpose |
|--------|-----|---------|
| Canvas | Canvas Tab | Main drawing screen with contribution calendar grid |
| Characters | Characters Tab | Character pattern selector and preview |
| Settings | Settings Tab | GitHub login, language, year, about |
| Create Repo Modal | Overlay | Modal dialog for creating GitHub repository |

## Color Palette

### GitHub Green Theme (Primary)
- Primary: `#216e39` (light) / `#39d353` (dark) — GitHub contribution green
- Background: `#f6f8fa` (light) / `#0d1117` (dark) — GitHub-style backgrounds
- Surface: `#ffffff` (light) / `#161b22` (dark) — Cards and elevated surfaces
- Foreground: `#1f2328` (light) / `#e6edf3` (dark) — Primary text
- Muted: `#656d76` (light) / `#8b949e` (dark) — Secondary text
- Border: `#d0d7de` (light) / `#30363d` (dark) — Dividers

### Contribution Levels (GitHub standard)
- Level 0: `#ebedf0` (light) / `#161b22` (dark) — No contributions
- Level 1: `#9be9a8` (light) / `#0e4429` (dark)
- Level 2: `#40c463` (light) / `#006d32` (dark)
- Level 3: `#30a14e` (light) / `#26a641` (dark)
- Level 4: `#216e39` (light) / `#39d353` (dark)

## MIUIX Design Tokens

| Token | Value |
|-------|-------|
| Card corner radius | 16dp |
| Button corner radius | 20dp |
| Input corner radius | 12dp |
| Horizontal padding | 16dp |
| Vertical item spacing | 8-12dp |
| Large title size | 28sp bold |
| Section label size | 12sp, theme color |
| Body text size | 16sp |
| Bottom nav | Icons + text + underline indicator |

## Screen Designs

### Canvas Screen (Main)
- **Top**: Large title "Canvas" (MIUIX style, left-aligned, 28sp bold)
- **Year selector**: Horizontal pill buttons for year selection
- **Calendar grid**: 52 weeks × 7 days, horizontal ScrollView, each cell ~12×12dp with 2dp gap
- **Toolbar card**: MIUIX-style card (16dp radius) containing:
  - Pen/Eraser toggle (pill-shaped segmented control)
  - Intensity selector (4 green squares: 1/3/6/9)
  - Action buttons row: All Green, Reset, Undo, Redo
- **Bottom**: "Create Repo" primary button (20dp radius, green fill)
- **Legend**: Less ▪▪▪▪▪ More at bottom of calendar

### Characters Screen
- **Top**: Large title "Characters" (MIUIX style)
- **Category tabs**: Horizontal pill tabs (Uppercase / Lowercase / Numbers / Symbols)
- **Character grid**: 6 columns, each character shown as a mini contribution pattern preview
- **Selected character preview**: Large preview card showing the pattern on calendar grid
- **Place button**: "Tap on calendar to place" instruction text

### Settings Screen
- **Top**: Large title "Settings" (MIUIX style)
- **Account section**: MIUIX card with:
  - GitHub avatar + username (if logged in)
  - "Not logged in" state with Login button
- **Token input**: MIUIX TextField with title "Personal Access Token"
- **Preferences card**:
  - Language: Arrow item → zh/en toggle
  - Year: Arrow item → year picker
- **About card**:
  - Version info
  - GitHub link

### Create Repo Modal
- MIUIX-style dialog (centered, 16dp radius, semi-transparent overlay)
- Repository name input
- Description input
- Private/Public toggle (MIUIX Switch)
- Cancel + "Generate & Push" buttons (MIUIX button style)

## Key User Flows

### Drawing Flow
1. User opens Canvas tab → sees empty calendar grid for current year
2. Pen tool is selected by default, intensity = 1
3. User taps/drags on cells → cells fill with green at selected intensity
4. User can switch to eraser to clear cells
5. User can adjust intensity (1/3/6/9) to change contribution count
6. Undo/Redo available for all operations

### Character Placement Flow
1. User opens Characters tab → sees character categories
2. Taps a category (e.g., Uppercase) → sees character grid
3. Taps a character (e.g., "A") → sees preview on calendar
4. Taps "Place on Canvas" → switches to Canvas tab with character applied

### Repository Creation Flow
1. User taps "Create Repo" on Canvas
2. If not logged in → redirected to Settings to enter PAT
3. If logged in → Create Repo modal appears
4. User fills in repo name, description, visibility
5. Taps "Generate & Push" → app creates repo via GitHub API
6. Progress indicator shown → success/error feedback

## Navigation
- 3 tabs at bottom: Canvas (brush icon), Characters (text icon), Settings (gear icon)
- MIUIX-style bottom navigation with icon + text + underline indicator on active tab
