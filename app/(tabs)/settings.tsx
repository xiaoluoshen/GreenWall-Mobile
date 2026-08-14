import React, { useState, useEffect, useCallback } from "react";
import {
  View,
  Text,
  ScrollView,
  TextInput,
  Alert,
  ActivityIndicator,
  StyleSheet,
  Linking,
} from "react-native";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
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
import { useI18n } from "@/lib/i18n";
import {
  getSavedToken,
  getSavedUser,
  saveToken,
  clearToken,
  validateToken,
  type GitHubUser,
} from "@/lib/github-api";

export default function SettingsScreen() {
  const colors = useColors();
  const { t, language, setLanguage } = useI18n();

  const [token, setToken] = useState("");
  const [user, setUser] = useState<GitHubUser | null>(null);
  const [loading, setLoading] = useState(false);
  const [showToken, setShowToken] = useState(false);

  useEffect(() => {
    loadSavedData();
  }, []);

  const loadSavedData = async () => {
    const savedUser = await getSavedUser();
    const savedToken = await getSavedToken();
    if (savedUser) setUser(savedUser);
    if (savedToken) setToken(savedToken);
  };

  const handleLogin = useCallback(async () => {
    if (!token.trim()) return;
    setLoading(true);

    const validatedUser = await validateToken(token.trim());
    if (validatedUser) {
      await saveToken(token.trim());
      setUser(validatedUser);
      Alert.alert(t.common.success, t.settings.loginSuccess);
    } else {
      Alert.alert(t.common.error, t.settings.loginError);
    }

    setLoading(false);
  }, [token, t]);

  const handleLogout = useCallback(async () => {
    await clearToken();
    setUser(null);
    setToken("");
    setShowToken(false);
  }, []);

  const toggleLanguage = useCallback(() => {
    setLanguage(language === "en" ? "zh" : "en");
  }, [language, setLanguage]);

  return (
    <ScreenContainer>
      <ScrollView
        style={{ flex: 1 }}
        contentContainerStyle={{ paddingBottom: 100 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Title */}
        <MaterialLargeTitle style={{ marginTop: 8 }}>{t.settings.title}</MaterialLargeTitle>

        {/* Account Section */}
        <MaterialSectionLabel>{t.settings.account}</MaterialSectionLabel>
        <MaterialCard style={{ padding: 0 }}>
          {user ? (
            <>
              <MaterialListItem
                title={user.name || user.login}
                subtitle={`${t.settings.loggedInAs} @${user.login}`}
                showArrow={false}
                leftIcon={
                  <MaterialIcons
                    name="account-circle"
                    size={24}
                    color={colors.primary}
                  />
                }
              />
              <MaterialDivider style={{ marginHorizontal: 0 }} />
              <View style={{ padding: 16 }}>
                <MaterialButton
                  title={t.settings.logout}
                  variant="secondary"
                  onPress={handleLogout}
                />
              </View>
            </>
          ) : (
            <>
              <MaterialListItem
                title={t.settings.notLoggedIn}
                subtitle={t.settings.tokenHint}
                showArrow={false}
                leftIcon={
                  <MaterialIcons
                    name="account-circle"
                    size={24}
                    color={colors.muted}
                  />
                }
              />
              <MaterialDivider style={{ marginHorizontal: 0 }} />
              <View style={{ padding: 16 }}>
                <Text
                  style={[styles.inputLabel, { color: colors.muted }]}
                >
                  {t.settings.tokenLabel}
                </Text>
                <TextInput
                  value={token}
                  onChangeText={setToken}
                  placeholder={t.settings.tokenPlaceholder}
                  placeholderTextColor={colors.muted}
                  secureTextEntry={!showToken}
                  autoCapitalize="none"
                  autoCorrect={false}
                  returnKeyType="done"
                  onSubmitEditing={handleLogin}
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
                    title={showToken ? t.settings.hideToken : t.settings.showToken}
                    variant="text"
                    compact
                    onPress={() => setShowToken(!showToken)}
                  />
                </View>
                {loading ? (
                  <ActivityIndicator
                    color={colors.primary}
                    style={{ marginTop: 12 }}
                  />
                ) : (
                  <MaterialButton
                    title={t.settings.login}
                    variant="primary"
                    onPress={handleLogin}
                    disabled={!token.trim()}
                    style={{ marginTop: 12 }}
                  />
                )}
              </View>
            </>
          )}
        </MaterialCard>

        {/* Preferences */}
        <MaterialSectionLabel>{t.settings.language}</MaterialSectionLabel>
        <MaterialCard style={{ padding: 0 }}>
          <MaterialListItem
            title={t.settings.language}
            rightText={language === "en" ? "English" : "中文"}
            onPress={toggleLanguage}
          />
        </MaterialCard>

        {/* About */}
        <MaterialSectionLabel>{t.settings.about}</MaterialSectionLabel>
        <MaterialCard style={{ padding: 0 }}>
          <MaterialListItem
            title={t.settings.version}
            rightText="1.3.0"
            showArrow={false}
          />
          <MaterialDivider style={{ marginHorizontal: 0 }} />
          <MaterialListItem
            title={t.settings.github}
            rightText="zmrlft/GreenWall"
            onPress={() =>
              Linking.openURL("https://github.com/zmrlft/GreenWall")
            }
            leftIcon={
              <MaterialIcons
                name="code"
                size={24}
                color={colors.muted}
              />
            }
          />
          <MaterialDivider style={{ marginHorizontal: 0 }} />
          <MaterialListItem
            title={t.settings.myRepos}
            rightText="xiaoluoshen/GreenWall-Mobile"
            onPress={() =>
              Linking.openURL("https://github.com/xiaoluoshen/GreenWall-Mobile")
            }
            leftIcon={
              <MaterialIcons
                name="folder"
                size={24}
                color={colors.muted}
              />
            }
          />
          <MaterialDivider style={{ marginHorizontal: 0 }} />
          <MaterialListItem
            title={t.settings.telegram}
            rightText="@lsposed0"
            onPress={() =>
              Linking.openURL("https://t.me/lsposed0")
            }
            leftIcon={
              <MaterialIcons
                name="send"
                size={24}
                color={colors.muted}
              />
            }
          />
          <MaterialDivider style={{ marginHorizontal: 0 }} />
          <MaterialListItem
            title={t.settings.greenWallMobile}
            subtitle={t.settings.aboutSubtitle}
            showArrow={false}
            leftIcon={
              <MaterialIcons
                name="eco"
                size={24}
                color={colors.primary}
              />
            }
          />
        </MaterialCard>
      </ScrollView>
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
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
});
