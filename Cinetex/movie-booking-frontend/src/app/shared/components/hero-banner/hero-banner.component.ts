import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-hero-banner',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './hero-banner.component.html',
  styleUrls: ['./hero-banner.component.scss']
})
export class HeroBannerComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() backgroundImage = '';
  @Input() ctaText?: string;
  @Input() ctaAriaLabel?: string;
  @Input() ctaLink?: string; // optional navigation link
}