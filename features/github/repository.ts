import { getGitHubErrorMessage, githubFetch } from "./client";
import type { CreateRepositoryOptions, CreateRepositoryResult } from "./types";

export async function createRepository(
  token: string,
  options: CreateRepositoryOptions,
): Promise<CreateRepositoryResult> {
  try {
    const response = await githubFetch("/user/repos", token, {
      method: "POST",
      body: JSON.stringify({
        name: options.name,
        description: options.description,
        private: options.isPrivate,
        auto_init: true,
      }),
    });

    if (!response.ok) {
      return {
        success: false,
        message: await getGitHubErrorMessage(response),
      };
    }

    const repository = (await response.json()) as { html_url?: unknown };
    return {
      success: true,
      message: "Repository created",
      htmlUrl: typeof repository.html_url === "string" ? repository.html_url : undefined,
    };
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : String(error);
    return { success: false, message: message || "Network error" };
  }
}
