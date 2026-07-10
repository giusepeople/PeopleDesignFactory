import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPartita } from './admin-partita';

describe('AdminPartita', () => {
  let component: AdminPartita;
  let fixture: ComponentFixture<AdminPartita>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPartita],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminPartita);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
