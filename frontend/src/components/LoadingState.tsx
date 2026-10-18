export function LoadingState({ label }: { label: string }) {
  return (
    <div className="status status-info" role="status">
      {label}
    </div>
  );
}
