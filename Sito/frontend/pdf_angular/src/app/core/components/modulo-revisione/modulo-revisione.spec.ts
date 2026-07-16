import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModuloRevisione } from './modulo-revisione';

describe('ModuloRevisione', () => {
  let component: ModuloRevisione;
  let fixture: ComponentFixture<ModuloRevisione>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModuloRevisione],
    }).compileComponents();

    fixture = TestBed.createComponent(ModuloRevisione);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
