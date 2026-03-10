import React, { createContext, useContext, useState, useCallback, useMemo } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export type Language = "en" | "zh";

const STORAGE_KEY = "greenwall_language";

type Translations = {
  canvas: {
    title: string;
    contributions: string;
    pen: string;
    eraser: string;
    intensity: string;
    manual: string;
    auto: string;
    allGreen: string;
    reset: string;
    undo: string;
    redo: string;
    createRepo: string;
    tooltipNone: string;
    tooltipSome: string;
    tooltipFuture: string;
    legendLess: string;
    legendMore: string;
  };
  characters: {
    title: string;
    uppercase: string;
    lowercase: string;
    numbers: string;
    symbols: string;
    tapToPlace: string;
    preview: string;
  };
  settings: {
    title: string;
    account: string;
    login: string;
    logout: string;
    tokenLabel: string;
    tokenPlaceholder: string;
    tokenHint: string;
    remember: string;
    language: string;
    year: string;
    about: string;
    version: string;
    loginSuccess: string;
    loginError: string;
    loggedInAs: string;
    notLoggedIn: string;
  };
  repo: {
    title: string;
    name: string;
    namePlaceholder: string;
    description: string;
    descriptionPlaceholder: string;
    private: string;
    public: string;
    confirm: string;
    cancel: string;
    generating: string;
    success: string;
    error: string;
    loginRequired: string;
    noContributions: string;
  };
  common: {
    confirm: string;
    cancel: string;
    loading: string;
    error: string;
    success: string;
  };
};

const en: Translations = {
  canvas: {
    title: "Canvas",
    contributions: "{{count}} contributions in {{year}}",
    pen: "Pen",
    eraser: "Eraser",
    intensity: "Intensity",
    manual: "Manual",
    auto: "Auto",
    allGreen: "All Green",
    reset: "Reset",
    undo: "Undo",
    redo: "Redo",
    createRepo: "Create Repo",
    tooltipNone: "No contributions on {{date}}",
    tooltipSome: "{{count}} contributions on {{date}}",
    tooltipFuture: "Future date {{date}}",
    legendLess: "Less",
    legendMore: "More",
  },
  characters: {
    title: "Characters",
    uppercase: "Uppercase",
    lowercase: "Lowercase",
    numbers: "Numbers",
    symbols: "Symbols",
    tapToPlace: "Tap on calendar to place",
    preview: "Preview",
  },
  settings: {
    title: "Settings",
    account: "GitHub Account",
    login: "Log In",
    logout: "Log Out",
    tokenLabel: "Personal Access Token",
    tokenPlaceholder: "ghp_xxxxxxxxxxxx",
    tokenHint: "Generate a token at GitHub Settings > Developer settings > Personal access tokens",
    remember: "Remember token",
    language: "Language",
    year: "Year",
    about: "About",
    version: "Version",
    loginSuccess: "Login successful",
    loginError: "Login failed. Please check your token.",
    loggedInAs: "Logged in as",
    notLoggedIn: "Not logged in",
  },
  repo: {
    title: "Create Remote Repository",
    name: "Repository Name",
    namePlaceholder: "my-contributions-2026",
    description: "Description",
    descriptionPlaceholder: "Generated with GreenWall",
    private: "Private",
    public: "Public",
    confirm: "Generate & Push",
    cancel: "Cancel",
    generating: "Generating...",
    success: "Repository created successfully!",
    error: "Failed to create repository: {{message}}",
    loginRequired: "Please log in to GitHub first",
    noContributions: "No contributions to push. Draw something first!",
  },
  common: {
    confirm: "Confirm",
    cancel: "Cancel",
    loading: "Loading...",
    error: "Error",
    success: "Success",
  },
};

const zh: Translations = {
  canvas: {
    title: "画布",
    contributions: "{{year}} 年共 {{count}} 次贡献",
    pen: "画笔",
    eraser: "橡皮擦",
    intensity: "强度",
    manual: "手动",
    auto: "自动",
    allGreen: "全绿",
    reset: "重置",
    undo: "撤销",
    redo: "重做",
    createRepo: "创建仓库",
    tooltipNone: "{{date}} 无贡献",
    tooltipSome: "{{date}} 共 {{count}} 次贡献",
    tooltipFuture: "未来日期 {{date}}",
    legendLess: "较少",
    legendMore: "更多",
  },
  characters: {
    title: "字符",
    uppercase: "大写",
    lowercase: "小写",
    numbers: "数字",
    symbols: "符号",
    tapToPlace: "点击日历放置字符",
    preview: "预览",
  },
  settings: {
    title: "设置",
    account: "GitHub 账户",
    login: "登录",
    logout: "退出登录",
    tokenLabel: "个人访问令牌",
    tokenPlaceholder: "ghp_xxxxxxxxxxxx",
    tokenHint: "在 GitHub 设置 > 开发者设置 > 个人访问令牌 中生成",
    remember: "记住令牌",
    language: "语言",
    year: "年份",
    about: "关于",
    version: "版本",
    loginSuccess: "登录成功",
    loginError: "登录失败，请检查令牌是否正确",
    loggedInAs: "已登录为",
    notLoggedIn: "未登录",
  },
  repo: {
    title: "创建远程仓库",
    name: "仓库名称",
    namePlaceholder: "my-contributions-2026",
    description: "描述",
    descriptionPlaceholder: "由 GreenWall 生成",
    private: "私有",
    public: "公开",
    confirm: "生成并推送",
    cancel: "取消",
    generating: "生成中...",
    success: "仓库创建成功！",
    error: "创建仓库失败：{{message}}",
    loginRequired: "请先登录 GitHub",
    noContributions: "没有贡献数据可推送，请先绘制图案！",
  },
  common: {
    confirm: "确认",
    cancel: "取消",
    loading: "加载中...",
    error: "错误",
    success: "成功",
  },
};

const translations: Record<Language, Translations> = { en, zh };

type I18nContextType = {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: Translations;
};

const I18nContext = createContext<I18nContextType>({
  language: "en",
  setLanguage: () => {},
  t: en,
});

export function I18nProvider({ children }: { children: React.ReactNode }) {
  const [language, setLanguageState] = useState<Language>("en");
  const [loaded, setLoaded] = useState(false);

  React.useEffect(() => {
    AsyncStorage.getItem(STORAGE_KEY).then((val) => {
      if (val === "zh" || val === "en") {
        setLanguageState(val);
      }
      setLoaded(true);
    });
  }, []);

  const setLanguage = useCallback((lang: Language) => {
    setLanguageState(lang);
    AsyncStorage.setItem(STORAGE_KEY, lang);
  }, []);

  const t = useMemo(() => translations[language], [language]);

  const value = useMemo(
    () => ({ language, setLanguage, t }),
    [language, setLanguage, t]
  );

  if (!loaded) return null;

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function useI18n() {
  return useContext(I18nContext);
}

export function interpolate(template: string, vars: Record<string, string | number>): string {
  let result = template;
  for (const [key, value] of Object.entries(vars)) {
    result = result.replace(new RegExp(`\\{\\{${key}\\}\\}`, "g"), String(value));
  }
  return result;
}
