import { BrowserModule } from '@angular/platform-browser';
import { Injector, NgModule } from '@angular/core';
import { createCustomElement } from '@angular/elements';
import { AppComponent } from './app.component';
import { MainComponent } from './main/main.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';




@NgModule({
  declarations: [
    AppComponent,
     MainComponent
  ],
  imports: [
    BrowserAnimationsModule,
    MatFormFieldModule,
    BrowserModule,
    MatInputModule,
    FormsModule,
    MatIconModule,
    MatSliderModule,
    MatButtonModule,
    HttpClientModule,
    MatDialogModule
  ],
  entryComponents: [MainComponent],
  providers: [],
})
export class AppModule {
  constructor(private injector: Injector) {}

  ngDoBootstrap() {
    console.log("Registering custom element with name: app-stocks-actions")
    const customElement = createCustomElement(MainComponent, {injector: this.injector})
    customElements.get('app-stocks-actions') || customElements.define('app-stocks-actions', customElement)
  }
}