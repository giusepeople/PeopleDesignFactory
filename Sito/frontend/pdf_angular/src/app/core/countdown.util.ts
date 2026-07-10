export function formatSecondi(totale: number | null): string {
  if (totale === null || totale < 0) return '--:--';
  const minuti = Math.floor(totale / 60);
  const secondi = totale % 60;
  return `${minuti.toString().padStart(2, '0')}:${secondi.toString().padStart(2, '0')}`;
}