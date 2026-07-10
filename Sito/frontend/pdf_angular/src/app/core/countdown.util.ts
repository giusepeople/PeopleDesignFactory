export function formatSecondi(totale: number | null): string {
  if (totale === null || totale < 0) return '--:--';
  const minuti = Math.floor(totale / 60);
  const secondi = totale % 60;
  return `${minuti.toString().padStart(2, '0')}:${secondi.toString().padStart(2, '0')}`;
}

/**
 * Ancora di sincronizzazione del countdown: invece di decrementare un contatore
 * locale (che accumula drift ed è vulnerabile al throttling dei tab in background),
 * calcoliamo ad ogni tick il tempo rimanente confrontando un istante di fine fase
 * assoluto con l'orologio corrente, corretto per lo sfasamento con il server.
 */
export class CountdownSync {
  private fineFaseMs: number | null = null;
  private offsetClientServerMs = 0;

  /**
   * Da chiamare ogni volta che arriva una risposta fresca da /fase-corrente.
   */
  aggiorna(faseIniziataIl: string | null, durataMinuti: number | undefined, serverTimestamp: string | null) {
    if (!faseIniziataIl || !durataMinuti) {
      this.fineFaseMs = null;
      return;
    }

    const inizioMs = new Date(faseIniziataIl).getTime();
    this.fineFaseMs = inizioMs + durataMinuti * 60_000;

    if (serverTimestamp) {
      const serverMs = new Date(serverTimestamp).getTime();
      this.offsetClientServerMs = serverMs - Date.now();
    }
  }

  /**
   * Da chiamare ad ogni tick (es. ogni secondo) per ottenere i secondi rimanenti aggiornati.
   */
  secondiRimanenti(): number | null {
    if (this.fineFaseMs === null) return null;
    const adessoCorretto = Date.now() + this.offsetClientServerMs;
    const restantiMs = this.fineFaseMs - adessoCorretto;
    return Math.max(0, Math.round(restantiMs / 1000));
  }
}