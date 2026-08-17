import { ActivityIndicator, Modal, StyleSheet, Text, TextInput, View } from "react-native";
import { MaterialButton, MaterialSwitch } from "@/components/material-ui";
import { useColors } from "@/hooks/use-colors";
import { useI18n } from "@/lib/i18n";

interface RepositoryPublishModalProps {
  visible: boolean;
  repositoryName: string;
  description: string;
  isPrivate: boolean;
  isSubmitting: boolean;
  progress: { current: number; total: number };
  onRepositoryNameChange: (value: string) => void;
  onDescriptionChange: (value: string) => void;
  onPrivacyChange: (value: boolean) => void;
  onCancel: () => void;
  onSubmit: () => void;
}

export function RepositoryPublishModal({
  visible,
  repositoryName,
  description,
  isPrivate,
  isSubmitting,
  progress,
  onRepositoryNameChange,
  onDescriptionChange,
  onPrivacyChange,
  onCancel,
  onSubmit,
}: RepositoryPublishModalProps) {
  const colors = useColors();
  const { t } = useI18n();

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={() => !isSubmitting && onCancel()}
    >
      <View style={styles.overlay}>
        <View style={[styles.content, { backgroundColor: colors.surface }]}>
          <Text style={[styles.title, { color: colors.foreground }]}>
            {t.repo.title}
          </Text>

          <Text style={[styles.inputLabel, { color: colors.muted }]}>
            {t.repo.name}
          </Text>
          <TextInput
            value={repositoryName}
            onChangeText={onRepositoryNameChange}
            placeholder={t.repo.namePlaceholder}
            placeholderTextColor={colors.muted}
            editable={!isSubmitting}
            autoCapitalize="none"
            autoCorrect={false}
            style={[
              styles.input,
              {
                color: colors.foreground,
                backgroundColor: colors.background,
                borderColor: colors.border,
              },
            ]}
          />

          <Text style={[styles.inputLabel, { color: colors.muted }]}>
            {t.repo.description}
          </Text>
          <TextInput
            value={description}
            onChangeText={onDescriptionChange}
            placeholder={t.repo.descriptionPlaceholder}
            placeholderTextColor={colors.muted}
            editable={!isSubmitting}
            style={[
              styles.input,
              {
                color: colors.foreground,
                backgroundColor: colors.background,
                borderColor: colors.border,
              },
            ]}
          />

          <View style={styles.switchRow}>
            <Text style={[styles.switchLabel, { color: colors.foreground }]}>
              {isPrivate ? t.repo.private : t.repo.public}
            </Text>
            <MaterialSwitch value={isPrivate} onValueChange={onPrivacyChange} />
          </View>

          {isSubmitting && (
            <View style={styles.progressRow}>
              <ActivityIndicator color={colors.primary} />
              <Text style={[styles.progressText, { color: colors.muted }]}>
                {t.repo.generating}
                {progress.total > 0 ? ` (${progress.current}/${progress.total})` : ""}
              </Text>
            </View>
          )}

          <View style={styles.buttonRow}>
            <MaterialButton
              title={t.repo.cancel}
              variant="secondary"
              onPress={onCancel}
              disabled={isSubmitting}
              style={styles.button}
            />
            <MaterialButton
              title={t.repo.confirm}
              variant="primary"
              onPress={onSubmit}
              disabled={isSubmitting || !repositoryName.trim()}
              style={styles.button}
            />
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },
  content: {
    width: "100%",
    maxWidth: 420,
    borderRadius: 20,
    padding: 28,
  },
  title: {
    fontSize: 22,
    fontWeight: "700",
    marginBottom: 24,
  },
  inputLabel: {
    fontSize: 14,
    fontWeight: "600",
    marginBottom: 8,
    marginTop: 16,
  },
  input: {
    borderWidth: 1,
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 12,
    fontSize: 16,
  },
  switchRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop: 16,
  },
  switchLabel: {
    fontSize: 16,
  },
  progressRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginTop: 16,
  },
  progressText: {
    fontSize: 15,
    fontWeight: "500",
  },
  buttonRow: {
    flexDirection: "row",
    gap: 12,
    marginTop: 28,
  },
  button: {
    flex: 1,
  },
});
