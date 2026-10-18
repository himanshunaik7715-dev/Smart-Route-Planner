export function ErrorNotice({ message }: { message: string }) {
  return (
    <div className="status status-error" role="alert">
      {message}
    </div>
  );
}
