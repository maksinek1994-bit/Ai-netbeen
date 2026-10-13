export type ActiveTab = 'ADVISOR' | 'CALCULATORS' | 'HACCP' | 'MENU' | 'STUDIO' | 'SETTINGS';

export interface ChatMessage {
  id: string;
  role: 'user' | 'model';
  content: string;
  timestamp: number;
  category?: 'general' | 'finance' | 'haccp' | 'menu' | 'brainstorm';
}

export interface Ingredient {
  id: string;
  name: string;
  unit: string; // kg, g, l, ml, szt
  packagePrice: number; // zł
  packageSize: number; // np. 1 kg lub 1000 g
  usedAmount: number; // ilość w porcji
  wastePct: number; // % ubytku / obierki
}

export interface Recipe {
  id: string;
  name: string;
  category: string;
  servings: number;
  ingredients: Ingredient[];
  targetFoodCostPct: number; // np. 28%
  sellingPriceGross: number; // zł brutto
  vatRate: number; // np. 8%
}

export interface BepCalculation {
  fixedMonthlyCosts: number; // czynsz, rachunki, pensje stałe, leasing
  averageTicketNet: number; // średni rachunek netto
  foodCostPct: number; // % kosztu produktów (np. 28%)
  otherVariableCostPct: number; // opakowania, prowizje terminali, media zmienne (np. 7%)
}

export interface LaborCostCalculation {
  monthlyRevenueNet: number;
  totalPayrollGross: number; // wypłaty, ZUS, podatki
  targetLaborCostPct: number; // np. 28-32%
}

export interface HaccpChecklistItem {
  id: string;
  category: 'sanepid_odbior' | 'gmp_ghp' | 'haccp_ccp' | 'alergeny' | 'temperatury';
  title: string;
  description: string;
  legalBase: string;
  isCompleted: boolean;
}

export interface MenuItemAnalysis {
  id: string;
  name: string;
  category: string;
  foodCostNet: number;
  sellingPriceNet: number;
  marginNet: number; // sellingPriceNet - foodCostNet
  monthlySalesVolume: number; // liczba sprzedanych porcji
  classification: 'STAR' | 'WORKHORSE' | 'PUZZLE' | 'DOG'; // Gwiazda, Dojna Krowa, Zagadka, Pies
}

// NetBeans Code Studio models (preserved from original project)
export interface CodeFileEntity {
  id: string;
  projectId: string;
  fileName: string;
  fileExtension: string;
  content: string;
  isMain: boolean;
  updatedAt: number;
}

export interface ProjectEntity {
  id: string;
  name: string;
  description: string;
  category: string;
  createdAt: number;
  updatedAt: number;
}
