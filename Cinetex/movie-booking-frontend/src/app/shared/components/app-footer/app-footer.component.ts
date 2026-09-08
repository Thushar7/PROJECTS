import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  templateUrl: './app-footer.component.html',
  styleUrl: './app-footer.component.scss'
})
export class AppFooterComponent {
  readonly year = new Date().getFullYear();
  // Dummy link groups (non-clickable spans for now)
  readonly groups = signal([
    { heading: 'Explore', items: ['Now Showing', 'Coming Soon', 'Top Rated', 'Genres'] },
    { heading: 'Company', items: ['About Us', 'Careers', 'Press', 'Blog'] },
    { heading: 'Support', items: ['Help Center', 'Contact', 'Accessibility', 'Feedback'] },
  ]);
}
