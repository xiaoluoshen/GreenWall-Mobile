const GITHUB_API_URL = "https://api.github.com";

export async function githubFetch(
  path: string,
  token: string,
  options: RequestInit = {},
): Promise<Response> {
  return fetch(`${GITHUB_API_URL}${path}`, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/vnd.github.v3+json",
      "Content-Type": "application/json",
      ...(options.headers ?? {}),
    },
  });
}

export async function getGitHubErrorMessage(response: Response): Promise<string> {
  try {
    const body: unknown = await response.json();
    if (body && typeof body === "object" && "message" in body) {
      const message = (body as { message?: unknown }).message;
      if (typeof message === "string" && message) return message;
    }
  } catch {
    // The API can return a non-JSON error body. Fall back to the HTTP status.
  }

  return `HTTP ${response.status}`;
}
