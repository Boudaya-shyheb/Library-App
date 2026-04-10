import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { inject } from '@angular/core';
import { KeycloakService } from './app/core/keycloak.service';

const startApp = async () => {
  const keycloakService = new KeycloakService();
  await keycloakService.init();

  bootstrapApplication(AppComponent, {
    providers: [
      provideHttpClient(), 
      provideRouter(routes),
      { provide: KeycloakService, useValue: keycloakService }
    ]
  }).catch(err => console.error(err));
};

startApp();
