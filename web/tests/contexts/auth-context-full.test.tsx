import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, waitFor, act } from '@testing-library/react'
import React from 'react'

const mockExtractJwtData = vi.fn()
vi.mock('@/components/auth/jwt-decoder', () => ({
  extractJwtData: (token: string) => mockExtractJwtData(token)
}))

const mockRefreshToken = vi.fn()
vi.mock('@/components/api/user-api', () => ({
  refreshToken: (token: string) => mockRefreshToken(token)
}))

const mockPush = vi.fn()
vi.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockPush,
    replace: vi.fn(),
    refresh: vi.fn()
  })
}))

import { AuthProvider, useAuth } from '@/components/contexts/auth-context'

function TestComponent() {
  const { login, logout, isAuthenticated, user, token, isLoading, isExpired, expiration } = useAuth()

  return (
    <div>
      <span data-testid="loading">{isLoading ? 'loading' : 'loaded'}</span>
      <span data-testid="auth-status">{isAuthenticated ? 'authenticated' : 'not-authenticated'}</span>
      <span data-testid="user">{user || 'no-user'}</span>
      <span data-testid="token">{token || 'no-token'}</span>
      <span data-testid="expired">{isExpired ? 'expired' : 'valid'}</span>
      <span data-testid="expiration">{expiration ? expiration.toISOString() : 'no-expiration'}</span>
      <button onClick={() => login('test-jwt-token')} data-testid="login-btn">
        Login
      </button>
      <button onClick={logout} data-testid="logout-btn">
        Logout
      </button>
    </div>
  )
}

describe('AuthContext - Token Refresh & Expiry', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockPush.mockClear()
    
    const store: Record<string, string> = {}
    vi.stubGlobal('localStorage', {
      getItem: vi.fn((key: string) => store[key] || null),
      setItem: vi.fn((key: string, value: string) => { store[key] = value }),
      removeItem: vi.fn((key: string) => { delete store[key] }),
      clear: vi.fn(() => { Object.keys(store).forEach(k => delete store[k]) }),
      length: Object.keys(store).length,
      key: vi.fn((index: number) => Object.keys(store)[index] || null)
    })
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('Expired token handling', () => {
    it('should logout when token is expired', async () => {
      localStorage.setItem('authToken', 'expired-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: true,
        expiration: new Date(Date.now() - 1000)
      })

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(screen.getByTestId('loading')).toHaveTextContent('loaded')
      }, { timeout: 10000 })

      expect(screen.getByTestId('auth-status')).toHaveTextContent('not-authenticated')
      expect(mockPush).toHaveBeenCalledWith('/login')
    })

    it('should logout when token decoding fails', async () => {
      localStorage.setItem('authToken', 'invalid-token')
      
      mockExtractJwtData.mockReturnValue(null)

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(screen.getByTestId('loading')).toHaveTextContent('loaded')
      }, { timeout: 10000 })

      expect(screen.getByTestId('auth-status')).toHaveTextContent('not-authenticated')
    })
  })

  describe('Token refresh logic', () => {
    it('should refresh token when expiration is within 15 minutes', async () => {
      const now = Date.now()
      const expirationTime = now + (14 * 60 * 1000)
      
      localStorage.setItem('authToken', 'expiring-soon-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: new Date(expirationTime)
      })

      mockRefreshToken.mockResolvedValue({
        status: 200,
        data: { token: 'refreshed-token' }
      })

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(mockRefreshToken).toHaveBeenCalledWith('expiring-soon-token')
      }, { timeout: 10000 })
    })

    it('should handle refresh token returning 401 error', async () => {
      const now = Date.now()
      const expirationTime = now + (10 * 60 * 1000)
      
      localStorage.setItem('authToken', 'expiring-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: new Date(expirationTime)
      })

      mockRefreshToken.mockResolvedValue({
        status: 401
      })

      const consoleSpy = vi.spyOn(console, 'debug').mockImplementation(() => {})

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(mockRefreshToken).toHaveBeenCalled()
      }, { timeout: 10000 })

      consoleSpy.mockRestore()
    })

    it('should handle refresh token with error in response', async () => {
      const now = Date.now()
      const expirationTime = now + (5 * 60 * 1000)
      
      localStorage.setItem('authToken', 'expiring-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: new Date(expirationTime)
      })

      mockRefreshToken.mockResolvedValue({
        status: 500,
        error: 'Server error'
      })

      const consoleSpy = vi.spyOn(console, 'debug').mockImplementation(() => {})

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(mockRefreshToken).toHaveBeenCalled()
      }, { timeout: 10000 })

      consoleSpy.mockRestore()
    })

    it('should handle refresh token with no data', async () => {
      const now = Date.now()
      const expirationTime = now + (8 * 60 * 1000)
      
      localStorage.setItem('authToken', 'expiring-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: new Date(expirationTime)
      })

      mockRefreshToken.mockResolvedValue({
        status: 200,
        data: null
      })

      const consoleSpy = vi.spyOn(console, 'debug').mockImplementation(() => {})

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(mockRefreshToken).toHaveBeenCalled()
      }, { timeout: 10000 })

      consoleSpy.mockRestore()
    })

    it('should handle refresh token exception', async () => {
      const now = Date.now()
      const expirationTime = now + (12 * 60 * 1000)
      
      localStorage.setItem('authToken', 'expiring-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: new Date(expirationTime)
      })

      mockRefreshToken.mockRejectedValue(new Error('Network error'))

      const consoleSpy = vi.spyOn(console, 'debug').mockImplementation(() => {})

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(mockRefreshToken).toHaveBeenCalled()
      }, { timeout: 10000 })

      consoleSpy.mockRestore()
    })

    it('should NOT refresh token when expiration is more than 15 minutes away', async () => {
      const now = Date.now()
      const expirationTime = now + (20 * 60 * 1000)
      
      localStorage.setItem('authToken', 'valid-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: new Date(expirationTime)
      })

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(screen.getByTestId('loading')).toHaveTextContent('loaded')
      }, { timeout: 10000 })

      expect(mockRefreshToken).not.toHaveBeenCalled()
      expect(screen.getByTestId('auth-status')).toHaveTextContent('authenticated')
    })

    it('should not refresh when no expiration date exists', async () => {
      localStorage.setItem('authToken', 'no-exp-token')
      
      mockExtractJwtData.mockReturnValue({
        username: 'test-user',
        isExpired: false,
        expiration: null
      })

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      )

      await waitFor(() => {
        expect(screen.getByTestId('loading')).toHaveTextContent('loaded')
      }, { timeout: 10000 })

      expect(mockRefreshToken).not.toHaveBeenCalled()
    })
  })
})
