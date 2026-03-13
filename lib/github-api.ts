import AsyncStorage from "@react-native-async-storage/async-storage";

const GITHUB_API = "https://api.github.com";
const TOKEN_KEY = "greenwall_github_token";
const USER_KEY = "greenwall_github_user";

export interface GitHubUser {
  login: string;
  avatar_url: string;
  name: string | null;
  email?: string | null;
}

export interface CreateRepoOptions {
  name: string;
  description: string;
  isPrivate: boolean;
}

export interface ContributionCommit {
  date: string;
  count: number;
}

async function getToken(): Promise<string | null> {
  return AsyncStorage.getItem(TOKEN_KEY);
}

async function githubFetch(
  path: string,
  token: string,
  options: RequestInit = {}
): Promise<Response> {
  return fetch(`${GITHUB_API}${path}`, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/vnd.github.v3+json",
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
  });
}

export async function saveToken(token: string): Promise<void> {
  await AsyncStorage.setItem(TOKEN_KEY, token);
}

export async function clearToken(): Promise<void> {
  await AsyncStorage.removeItem(TOKEN_KEY);
  await AsyncStorage.removeItem(USER_KEY);
}

export async function getSavedToken(): Promise<string | null> {
  return AsyncStorage.getItem(TOKEN_KEY);
}

export async function getSavedUser(): Promise<GitHubUser | null> {
  const data = await AsyncStorage.getItem(USER_KEY);
  if (data) {
    try {
      return JSON.parse(data);
    } catch {
      return null;
    }
  }
  return null;
}

/**
 * 获取用户的主邮箱（用于贡献提交，确保贡献能刷到账号）
 */
async function getUserPrimaryEmail(token: string, login: string): Promise<string> {
  try {
    const res = await githubFetch("/user/emails", token);
    if (res.ok) {
      const emails: Array<{ email: string; primary: boolean; verified: boolean }> = await res.json();
      const primary = emails.find((e) => e.primary && e.verified);
      if (primary) return primary.email;
      const verified = emails.find((e) => e.verified);
      if (verified) return verified.email;
    }
  } catch {
    // Ignore error
  }
  return `${login}@users.noreply.github.com`;
}

export async function validateToken(token: string): Promise<GitHubUser | null> {
  try {
    const res = await githubFetch("/user", token);
    if (res.ok) {
      const user: GitHubUser = await res.json();
      const email = await getUserPrimaryEmail(token, user.login);
      const userWithEmail: GitHubUser = { ...user, email };
      await AsyncStorage.setItem(USER_KEY, JSON.stringify(userWithEmail));
      return userWithEmail;
    }
    return null;
  } catch {
    return null;
  }
}

export async function createRepository(
  token: string,
  options: CreateRepoOptions
): Promise<{ success: boolean; message: string; html_url?: string }> {
  try {
    const res = await githubFetch("/user/repos", token, {
      method: "POST",
      body: JSON.stringify({
        name: options.name,
        description: options.description,
        private: options.isPrivate,
        auto_init: true,
      }),
    });

    if (res.ok) {
      const data = await res.json();
      return { success: true, message: "Repository created", html_url: data.html_url };
    }

    const error = await res.json();
    return {
      success: false,
      message: error.message || `HTTP ${res.status}`,
    };
  } catch (e: any) {
    return { success: false, message: e.message || "Network error" };
  }
}

export async function pushContributions(
  token: string,
  owner: string,
  repo: string,
  contributions: ContributionCommit[],
  onProgress?: (current: number, total: number) => void
): Promise<{ success: boolean; message: string }> {
  try {
    const email = await getUserPrimaryEmail(token, owner);
    const savedUser = await getSavedUser();
    const authorName = savedUser?.name || savedUser?.login || owner;

    const sorted = [...contributions].sort(
      (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
    );

    const total = sorted.reduce((sum, c) => sum + c.count, 0);
    let current = 0;

    for (const contrib of sorted) {
      for (let i = 0; i < contrib.count; i++) {
        const timestamp = new Date(contrib.date + "T12:00:00");
        timestamp.setMinutes(i % 60);
        timestamp.setSeconds(Math.floor(i / 60));

        const filePath = `contributions/${contrib.date}_${i}.txt`;
        const content = btoa(
          `GreenWall contribution\nDate: ${contrib.date}\nIndex: ${i + 1}\nGenerated: ${new Date().toISOString()}`
        );

        const res = await githubFetch(
          `/repos/${owner}/${repo}/contents/${filePath}`,
          token,
          {
            method: "PUT",
            body: JSON.stringify({
              message: `contribution: ${contrib.date}`,
              content,
              committer: {
                name: authorName,
                email: email,
                date: timestamp.toISOString(),
              },
              author: {
                name: authorName,
                email: email,
                date: timestamp.toISOString(),
              },
            }),
          }
        );

        if (!res.ok) {
          const error = await res.json();
          return {
            success: false,
            message: `Push failed ${contrib.date}: ${error.message || res.status}`,
          };
        }

        current++;
        onProgress?.(current, total);
      }
    }

    return { success: true, message: "All contributions pushed successfully" };
  } catch (e: any) {
    return { success: false, message: e.message || "Network error" };
  }
}
