import React, { useCallback } from "react";
import {
  ActivityIndicator,
  Alert,
  Linking,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import Constants from "expo-constants";
import { ScreenContainer } from "@/components/screen-container";
import { useColors } from "@/hooks/use-colors";
import {
  MaterialButton,
  MaterialCard,
  MaterialDivider,
  MaterialLargeTitle,
  MaterialListItem,
  MaterialSectionLabel,
} from "@/components/material-ui";
import { useGitHubSession } from "@/features/settings/hooks/use-github-session";
import { useI18n } from "@/lib/i18n";

const ORIGINAL_PROJECT_URL = "https://github.com/zmrlft/GreenWall";
const MOBILE_PROJECT_URL = "https://github.com/xiaoluoshen/GreenWall-Mobile";
const COMMUNITY_URL = "https://t.me/lsposed0";

export default function SettingsScreen() {
  const colors = useColors();
  const { t, language, setLanguage } = useI18n();
  const {
    token,
    setToken,
    user,
    isLoading,
    isSubmitting,
    isTokenVisible,
    toggleTokenVisibility,
    login,
    logout,
  } = useGitHubSession();
  const appVersion =
    Constants.nativeAppVersion ?? Constants.expoConfig?.version ?? "—";

  const handleLogin = useCallback(async () => {
    const didLogIn = await login();
    Alert.alert(
      didLogIn ? t.common.success : t.common.error,
      didLogIn ? t.settings.loginSuccess : t.settings.loginError,
    );
  }, [login, t]);

  const handleLogout = useCallback(async () => {
    await logout();
  }, [logout]);

  const toggleLanguage = useCallback(() => {
    setLanguage(language === "en" ? "zh" : "en");
  }, [language, setLanguage]);

  return (
    <ScreenContainer>
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        <MaterialLargeTitle style={styles.title}>{t.settings.title}</MaterialLargeTitle>

        <MaterialSectionLabel>{t.settings.account}</MaterialSectionLabel>
        <MaterialCard style={styles.flushCard}>
          {isLoading ? (
            <ActivityIndicator color={colors.primary} style={styles.loading} />
          ) : user ? (
            <LoggedInAccount
              displayName={user.name || user.login}
              login={user.login}
              onLogout={handleLogout}
            />
          ) : (
            <LoginForm
              token={token}
              isTokenVisible={isTokenVisible}
              isSubmitting={isSubmitting}
              onTokenChange={setToken}
              onToggleTokenVisibility={toggleTokenVisibility}
              onLogin={handleLogin}
            />
          )}
        </MaterialCard>

        <MaterialSectionLabel>{t.settings.language}</MaterialSectionLabel>
        <MaterialCard style={styles.flushCard}>
          <MaterialListItem
            title={t.settings.language}
            rightText={language === "en" ? "English" : "中文"}
            onPress={toggleLanguage}
          />
        </MaterialCard>

        <MaterialSectionLabel>{t.settings.about}</MaterialSectionLabel>
        <MaterialCard style={styles.flushCard}>
          <MaterialListItem
            title={t.settings.version}
            rightText={appVersion}
            showArrow={false}
          />
          <MaterialDivider style={styles.flushDivider} />
          <ExternalLinkItem
            title={t.settings.github}
            label="zmrlft/GreenWall"
            url={ORIGINAL_PROJECT_URL}
            icon="code"
          />
          <MaterialDivider style={styles.flushDivider} />
          <ExternalLinkItem
            title={t.settings.myRepos}
            label="xiaoluoshen/GreenWall-Mobile"
            url={MOBILE_PROJECT_URL}
            icon="folder"
          />
          <MaterialDivider style={styles.flushDivider} />
          <ExternalLinkItem
            title={t.settings.telegram}
            label="@lsposed0"
            url={COMMUNITY_URL}
            icon="send"
          />
          <MaterialDivider style={styles.flushDivider} />
          <MaterialListItem
            title={t.settings.greenWallMobile}
            subtitle={t.settings.aboutSubtitle}
            showArrow={false}
            leftIcon={<MaterialIcons name="eco" size={24} color={colors.primary} />}
          />
        </MaterialCard>
      </ScrollView>
    </ScreenContainer>
  );
}

function LoggedInAccount({
  displayName,
  login,
  onLogout,
}: {
  displayName: string;
  login: string;
  onLogout: () => void;
}) {
  const colors = useColors();
  const { t } = useI18n();

  return (
    <>
      <MaterialListItem
        title={displayName}
        subtitle={`${t.settings.loggedInAs} @${login}`}
        showArrow={false}
        leftIcon={<MaterialIcons name="account-circle" size={24} color={colors.primary} />}
      />
      <MaterialDivider style={styles.flushDivider} />
      <View style={styles.accountAction}>
        <MaterialButton title={t.settings.logout} variant="secondary" onPress={onLogout} />
      </View>
    </>
  );
}

function LoginForm({
  token,
  isTokenVisible,
  isSubmitting,
  onTokenChange,
  onToggleTokenVisibility,
  onLogin,
}: {
  token: string;
  isTokenVisible: boolean;
  isSubmitting: boolean;
  onTokenChange: (value: string) => void;
  onToggleTokenVisibility: () => void;
  onLogin: () => void;
}) {
  const colors = useColors();
  const { t } = useI18n();

  return (
    <>
      <MaterialListItem
        title={t.settings.notLoggedIn}
        subtitle={t.settings.tokenHint}
        showArrow={false}
        leftIcon={<MaterialIcons name="account-circle" size={24} color={colors.muted} />}
      />
      <MaterialDivider style={styles.flushDivider} />
      <View style={styles.form}>
        <Text style={[styles.inputLabel, { color: colors.muted }]}>
          {t.settings.tokenLabel}
        </Text>
        <TextInput
          value={token}
          onChangeText={onTokenChange}
          placeholder={t.settings.tokenPlaceholder}
          placeholderTextColor={colors.muted}
          secureTextEntry={!isTokenVisible}
          autoCapitalize="none"
          autoCorrect={false}
          returnKeyType="done"
          onSubmitEditing={onLogin}
          editable={!isSubmitting}
          style={[
            styles.tokenInput,
            {
              color: colors.foreground,
              backgroundColor: colors.background,
              borderColor: colors.border,
            },
          ]}
        />
        <View style={styles.tokenActions}>
          <MaterialButton
            title={isTokenVisible ? t.settings.hideToken : t.settings.showToken}
            variant="text"
            compact
            onPress={onToggleTokenVisibility}
          />
        </View>
        {isSubmitting ? (
          <ActivityIndicator color={colors.primary} style={styles.submitLoading} />
        ) : (
          <MaterialButton
            title={t.settings.login}
            variant="primary"
            onPress={onLogin}
            disabled={!token.trim()}
            style={styles.loginButton}
          />
        )}
      </View>
    </>
  );
}

function ExternalLinkItem({
  title,
  label,
  url,
  icon,
}: {
  title: string;
  label: string;
  url: string;
  icon: React.ComponentProps<typeof MaterialIcons>["name"];
}) {
  const colors = useColors();

  const openExternalUrl = useCallback(() => {
    void Linking.openURL(url);
  }, [url]);

  return (
    <MaterialListItem
      title={title}
      rightText={label}
      onPress={openExternalUrl}
      leftIcon={<MaterialIcons name={icon} size={24} color={colors.muted} />}
    />
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
  content: {
    paddingBottom: 100,
  },
  title: {
    marginTop: 8,
  },
  flushCard: {
    padding: 0,
  },
  flushDivider: {
    marginHorizontal: 0,
  },
  loading: {
    marginVertical: 32,
  },
  accountAction: {
    padding: 16,
  },
  form: {
    padding: 16,
  },
  inputLabel: {
    fontSize: 13,
    fontWeight: "500",
    marginBottom: 8,
  },
  tokenInput: {
    borderWidth: 1,
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 10,
    fontSize: 15,
    fontFamily: "monospace",
  },
  tokenActions: {
    flexDirection: "row",
    justifyContent: "flex-end",
    marginTop: 4,
  },
  submitLoading: {
    marginTop: 12,
  },
  loginButton: {
    marginTop: 12,
  },
});
