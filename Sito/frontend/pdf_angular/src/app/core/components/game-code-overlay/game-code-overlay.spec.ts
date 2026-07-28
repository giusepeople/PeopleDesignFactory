import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameCodeOverlay } from './game-code-overlay';

describe('GameCodeOverlay', () => {
  let component: GameCodeOverlay;
  let fixture: ComponentFixture<GameCodeOverlay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameCodeOverlay],
    }).compileComponents();

    fixture = TestBed.createComponent(GameCodeOverlay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
