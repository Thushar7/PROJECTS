// Lightweight JWT decode (no external dependency) just to read expiration (exp claim)
// Assumes token is well-formed. Fails quietly returning null when invalid.
export interface DecodedJwt { exp?: number; [k: string]: any; }

export function decodeJwt(token: string): DecodedJwt | null {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    const payload = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decodeURIComponent(escape(payload)));
  } catch {
    return null;
  }
}

export function isJwtExpired(token: string, skewSeconds = 5): boolean {
  const decoded = decodeJwt(token);
  if (!decoded?.exp) return false; // if unknown, don't block; server will enforce
  const nowSeconds = Math.floor(Date.now() / 1000);
  return decoded.exp <= (nowSeconds + skewSeconds);
}
