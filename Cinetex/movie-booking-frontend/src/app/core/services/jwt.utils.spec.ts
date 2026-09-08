import { decodeJwt, isJwtExpired } from './jwt.utils';

describe('jwt.utils', () => {
  // Helper to build a token with given payload (unsigned mock)
  function buildToken(payload: Record<string, any>) {
    const header = btoa(JSON.stringify({ alg: 'none', typ: 'JWT' }));
    const body = btoa(JSON.stringify(payload));
    return `${header}.${body}.`; // signature left blank intentionally
  }

  it('decodes exp claim', () => {
    const exp = Math.floor(Date.now() / 1000) + 60;
    const token = buildToken({ exp, foo: 'bar' });
    const decoded = decodeJwt(token);
    expect(decoded?.exp).toBe(exp);
  // Access via index signature
  // @ts-ignore
  expect(decoded && decoded['foo']).toBe('bar');
  });

  it('reports not expired when exp in future', () => {
    const exp = Math.floor(Date.now() / 1000) + 60;
    const token = buildToken({ exp });
    expect(isJwtExpired(token)).toBeFalse();
  });

  it('reports expired when exp in past', () => {
    const exp = Math.floor(Date.now() / 1000) - 10;
    const token = buildToken({ exp });
    expect(isJwtExpired(token)).toBeTrue();
  });

  it('handles invalid token gracefully', () => {
    expect(decodeJwt('bad.token.parts')).toBeNull();
  });
});
