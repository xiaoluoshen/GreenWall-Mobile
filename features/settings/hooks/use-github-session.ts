import { useCallback, useEffect, useState } from "react";
import {
  clearToken,
  getSavedToken,
  getSavedUser,
  saveToken,
  validateToken,
  type GitHubUser,
} from "@/features/github";

export function useGitHubSession() {
  const [token, setToken] = useState("");
  const [user, setUser] = useState<GitHubUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isTokenVisible, setIsTokenVisible] = useState(false);

  const loadSession = useCallback(async () => {
    setIsLoading(true);
    try {
      const [savedUser, savedToken] = await Promise.all([
        getSavedUser(),
        getSavedToken(),
      ]);
      setUser(savedUser);
      setToken(savedToken ?? "");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadSession();
  }, [loadSession]);

  const login = useCallback(async (): Promise<boolean> => {
    const normalizedToken = token.trim();
    if (!normalizedToken) return false;

    setIsSubmitting(true);
    try {
      const validatedUser = await validateToken(normalizedToken);
      if (!validatedUser) return false;

      await saveToken(normalizedToken);
      setUser(validatedUser);
      return true;
    } finally {
      setIsSubmitting(false);
    }
  }, [token]);

  const logout = useCallback(async () => {
    await clearToken();
    setUser(null);
    setToken("");
    setIsTokenVisible(false);
  }, []);

  return {
    token,
    setToken,
    user,
    isLoading,
    isSubmitting,
    isTokenVisible,
    toggleTokenVisibility: () => setIsTokenVisible((isVisible) => !isVisible),
    login,
    logout,
  };
}
