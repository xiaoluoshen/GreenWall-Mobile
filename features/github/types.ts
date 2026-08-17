export interface GitHubUser {
  login: string;
  avatar_url: string;
  name: string | null;
  email?: string | null;
}

export interface CreateRepositoryOptions {
  name: string;
  description: string;
  isPrivate: boolean;
}

export interface ContributionCommit {
  date: string;
  count: number;
}

export interface GitHubOperationResult {
  success: boolean;
  message: string;
}

export interface CreateRepositoryResult extends GitHubOperationResult {
  htmlUrl?: string;
}
