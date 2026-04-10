import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private keycloak: Keycloak | undefined;

  async init(): Promise<void> {
    this.keycloak = new Keycloak({
      url: 'http://localhost:8180',
      realm: 'library-realm',
      clientId: 'library-client'
    });

    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: false
      });

      console.log(`User is ${authenticated ? 'authenticated' : 'not authenticated'}`);
    } catch (error) {
      console.error('Failed to initialize Keycloak', error);
    }
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  logout(): void {
    this.keycloak?.logout();
  }

  getUserProfile() {
    return this.keycloak?.profile;
  }
}
