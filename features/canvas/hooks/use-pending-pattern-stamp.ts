import { useCallback, useRef } from "react";
import { useFocusEffect } from "expo-router";
import type { ContributionDay } from "@/features/contributions";
import { clearPendingPattern, readPendingPattern } from "@/features/patterns/pending-pattern";
import { createPatternStamp } from "@/lib/pattern-stamp";

interface UsePendingPatternStampOptions {
  days: ContributionDay[];
  isReady: boolean;
  commitBatch: (updates: Record<string, number>) => void;
}

export function usePendingPatternStamp({
  days,
  isReady,
  commitBatch,
}: UsePendingPatternStampOptions): void {
  const processedPatternRef = useRef<string | null>(null);

  useFocusEffect(
    useCallback(() => {
      let isActive = true;

      const applyPendingPattern = async () => {
        if (!isReady) return;

        const pendingPattern = await readPendingPattern();
        if (!isActive) return;
        if (!pendingPattern) {
          processedPatternRef.current = null;
          return;
        }

        const patternId = JSON.stringify(pendingPattern);
        if (processedPatternRef.current === patternId) return;

        const updates = createPatternStamp(days, pendingPattern.pattern);
        processedPatternRef.current = patternId;
        if (Object.keys(updates).length > 0) commitBatch(updates);
        await clearPendingPattern();
      };

      void applyPendingPattern();
      return () => {
        isActive = false;
      };
    }, [commitBatch, days, isReady]),
  );
}
