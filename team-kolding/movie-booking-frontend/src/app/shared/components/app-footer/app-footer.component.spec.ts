import { TestBed } from '@angular/core/testing';
import { AppFooterComponent } from './app-footer.component';

describe('AppFooterComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppFooterComponent]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(AppFooterComponent);
    const comp = fixture.componentInstance;
    expect(comp).toBeTruthy();
  });

  it('renders groups and year', () => {
    const fixture = TestBed.createComponent(AppFooterComponent);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelectorAll('.link-group').length).toBeGreaterThan(0);
    expect(el.textContent).toContain(new Date().getFullYear().toString());
  });
});
