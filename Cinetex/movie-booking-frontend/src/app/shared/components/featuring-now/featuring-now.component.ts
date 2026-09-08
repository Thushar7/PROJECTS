import { Component, ElementRef, ViewChild, AfterViewInit, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-featuring-now',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './featuring-now.component.html',
  styleUrls: ['./featuring-now.component.scss']
})
export class FeaturingNowComponent implements AfterViewInit {
  @ViewChild('featureVideo') videoEl?: ElementRef<HTMLVideoElement>;
  private platformId = inject(PLATFORM_ID);

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) return; // SSR safety
    const video = this.videoEl?.nativeElement;
    if (!video) return;
    // Ensure attributes for autoplay policy
    video.muted = true;
    video.playsInline = true;
    const attemptPlay = (retries = 3) => {
      const p = video.play();
      if (p && typeof p.then === 'function') {
        p.catch(() => {
          if (retries > 0) {
            setTimeout(() => attemptPlay(retries - 1), 400);
          }
        });
      }
    };
    attemptPlay();
  }
}
