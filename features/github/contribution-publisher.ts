import { getGitHubErrorMessage, githubFetch } from "./client";
import { getSavedUser, getUserPrimaryEmail } from "./session";
import type { ContributionCommit, GitHubOperationResult } from "./types";

const API_REQUEST_DELAY_MS = 100;

export async function publishContributions(
  token: string,
  owner: string,
  repository: string,
  contributions: ContributionCommit[],
  onProgress?: (current: number, total: number) => void,
): Promise<GitHubOperationResult> {
  try {
    const email = await getUserPrimaryEmail(token, owner);
    const savedUser = await getSavedUser();
    const authorName = savedUser?.name || savedUser?.login || owner;
    const sortedContributions = [...contributions].sort(
      (left, right) => new Date(left.date).getTime() - new Date(right.date).getTime(),
    );
    const total = sortedContributions.reduce(
      (sum, contribution) => sum + contribution.count,
      0,
    );

    let current = 0;
    for (const contribution of sortedContributions) {
      for (let index = 0; index < contribution.count; index += 1) {
        const response = await createContributionCommit({
          token,
          owner,
          repository,
          contribution,
          index,
          authorName,
          email,
        });

        if (!response.ok) {
          return {
            success: false,
            message: `Push failed on ${contribution.date}: ${await getGitHubErrorMessage(response)}`,
          };
        }

        current += 1;
        onProgress?.(current, total);
        await delay(API_REQUEST_DELAY_MS);
      }
    }

    return { success: true, message: "All contributions pushed successfully" };
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : String(error);
    console.warn(`Unable to publish GitHub contributions: ${message}`);
    return { success: false, message: message || "Network error" };
  }
}

interface CreateCommitRequest {
  token: string;
  owner: string;
  repository: string;
  contribution: ContributionCommit;
  index: number;
  authorName: string;
  email: string;
}

function createContributionCommit(request: CreateCommitRequest): Promise<Response> {
  const { contribution, index } = request;
  const timestamp = createContributionTimestamp(contribution.date, index);
  const filePath = `contributions/${contribution.date}/${String(index).padStart(4, "0")}.txt`;
  const content = btoa(
    [
      "GreenWall Contribution",
      `Date: ${contribution.date}`,
      `Index: ${index + 1}/${contribution.count}`,
      `Generated: ${new Date().toISOString()}`,
      `Author: ${request.authorName}`,
    ].join("\n"),
  );

  return githubFetch(`/repos/${request.owner}/${request.repository}/contents/${filePath}`, request.token, {
    method: "PUT",
    body: JSON.stringify({
      message: `chore: contribution on ${contribution.date} (${index + 1}/${contribution.count})`,
      content,
      committer: {
        name: request.authorName,
        email: request.email,
        date: timestamp.toISOString(),
      },
      author: {
        name: request.authorName,
        email: request.email,
        date: timestamp.toISOString(),
      },
    }),
  });
}

function createContributionTimestamp(date: string, index: number): Date {
  const timestamp = new Date(`${date}T00:00:00Z`);
  timestamp.setUTCHours(0, index % 60, Math.floor(index / 60), 0);
  return timestamp;
}

function delay(milliseconds: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, milliseconds));
}
