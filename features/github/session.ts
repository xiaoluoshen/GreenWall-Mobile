import AsyncStorage from "@react-native-async-storage/async-storage";
import { getGitHubErrorMessage, githubFetch } from "./client";
import type { GitHubUser } from "./types";

const TOKEN_STORAGE_KEY = "greenwall_github_token";
const USER_STORAGE_KEY = "greenwall_github_user";

export async function saveToken(token: string): Promise<void> {
  await AsyncStorage.setItem(TOKEN_STORAGE_KEY, token);
}

export async function clearToken(): Promise<void> {
  await AsyncStorage.multiRemove([TOKEN_STORAGE_KEY, USER_STORAGE_KEY]);
}

export async function getSavedToken(): Promise<string | null> {
  return AsyncStorage.getItem(TOKEN_STORAGE_KEY);
}

export async function getSavedUser(): Promise<GitHubUser | null> {
  const serializedUser = await AsyncStorage.getItem(USER_STORAGE_KEY);
  if (!serializedUser) return null;

  try {
    return JSON.parse(serializedUser) as GitHubUser;
  } catch {
    await AsyncStorage.removeItem(USER_STORAGE_KEY);
    return null;
  }
}

export async function validateToken(token: string): Promise<GitHubUser | null> {
  try {
    const response = await githubFetch("/user", token);
    if (!response.ok) return null;

    const user = (await response.json()) as GitHubUser;
    const email = await getUserPrimaryEmail(token, user.login);
    const userWithEmail = { ...user, email };
    await AsyncStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userWithEmail));
    return userWithEmail;
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : String(error);
    console.warn(`Unable to validate GitHub token: ${message}`);
    return null;
  }
}

export async function getUserPrimaryEmail(
  token: string,
  login: string,
): Promise<string> {
  try {
    const response = await githubFetch("/user/emails", token);
    if (response.ok) {
      const emails = (await response.json()) as Array<{
        email: string;
        primary: boolean;
        verified: boolean;
      }>;
      const preferredEmail =
        emails.find((email) => email.primary)?.email ??
        emails.find((email) => email.verified)?.email ??
        emails[0]?.email;

      if (preferredEmail) return preferredEmail;
    } else {
      console.warn(
        `Unable to load GitHub email: ${await getGitHubErrorMessage(response)}`,
      );
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : String(error);
    console.warn(`Unable to load GitHub email: ${message}`);
  }

  return `${login}@users.noreply.github.com`;
}
