import { Recipe, HaccpChecklistItem, MenuItemAnalysis, ProjectEntity, CodeFileEntity, ChatMessage } from '../types';

const STORAGE_KEYS = {
  CHAT_MESSAGES: 'gastro_chat_messages',
  RECIPES: 'gastro_recipes',
  HACCP_CHECKLIST: 'gastro_haccp_checklist',
  MENU_ITEMS: 'gastro_menu_items',
  PROJECTS: 'netbeans_projects',
  PROJECT_FILES: 'netbeans_project_files',
  ACTIVE_PROJECT_ID: 'netbeans_active_project_id',
  CUSTOM_API_KEY: 'gastro_gemini_api_key',
  BEP_DATA: 'gastro_bep_data',
  LABOR_DATA: 'gastro_labor_data',
};

// Initial Sample Chat Messages
const INITIAL_CHAT_MESSAGES: ChatMessage[] = [
  {
    id: 'msg_welcome_1',
    role: 'model',
    content: `Cześć! Jestem Twoim **doradcą gastronomicznym i partnerem biznesowym**.

Moim celem jest kompleksowe wsparcie Twojego lokalu:
• **Finanse i liczby:** Food Cost (kalkulacja do 1 grosza), próg rentowności BEP, marże, Labor Cost i optymalizacja grafików.
• **Prawo & Sanepid:** Odbiór lokalu krok po kroku, wdrożenie HACCP, dobre praktyki GHP/GMP, normy temperatur i wykaz 14 alergenów.
• **Menu & Kuchnia:** Inżynieria karty (wyrzucamy pozycje nieopłacalne, promujemy gwiazdy), receptury i minimalizacja strat (food waste).
• **Burza mózgów:** Koncepcja lokalu, nazewnictwo, unikalna propozycja sprzedaży (USP) i marketing.

Odpowiadam krótko, na konkretach i z wyliczeniami dopasowanymi do Twojego biznesu.

**W czym ruszamy na start?**
1. Masz konkretne danie do policzenia Food Costu?
2. Szykujesz lokal pod odbiór Sanepidu?
3. A może chcesz oszacować miesięczny próg rentowności (BEP)?`,
    timestamp: Date.now() - 3600000,
  },
];

// Initial Seed Recipes
const INITIAL_RECIPES: Recipe[] = [
  {
    id: 'rec_1',
    name: 'Burger Wołowy Klasyk z Frytkami',
    category: 'Dania Główne',
    servings: 1,
    targetFoodCostPct: 28,
    sellingPriceGross: 46.00,
    vatRate: 8,
    ingredients: [
      { id: 'ing_1', name: 'Wołowina mielona (antrykot/rostbef)', unit: 'kg', packagePrice: 42.00, packageSize: 1, usedAmount: 0.200, wastePct: 2 },
      { id: 'ing_2', name: 'Bułka maślana brioche', unit: 'szt', packagePrice: 3.20, packageSize: 1, usedAmount: 1, wastePct: 0 },
      { id: 'ing_3', name: 'Ser Cheddar dojrzały', unit: 'kg', packagePrice: 45.00, packageSize: 1, usedAmount: 0.040, wastePct: 0 },
      { id: 'ing_4', name: 'Bekon wędzony podsuszany', unit: 'kg', packagePrice: 38.00, packageSize: 1, usedAmount: 0.040, wastePct: 5 },
      { id: 'ing_5', name: 'Warzywa (sałata, pomidor, ogórek kiszony, cebula)', unit: 'porcja', packagePrice: 2.20, packageSize: 1, usedAmount: 1, wastePct: 10 },
      { id: 'ing_6', name: 'Sos autorski (majonez, musztarda, przyprawy)', unit: 'porcja', packagePrice: 1.10, packageSize: 1, usedAmount: 1, wastePct: 0 },
      { id: 'ing_7', name: 'Frytki stekowe + olej do smażenia', unit: 'kg', packagePrice: 9.50, packageSize: 1, usedAmount: 0.180, wastePct: 5 },
    ],
  },
  {
    id: 'rec_2',
    name: 'Pizza Margherita Rzemieślnicza (32 cm)',
    category: 'Pizza',
    servings: 1,
    targetFoodCostPct: 22,
    sellingPriceGross: 36.00,
    vatRate: 8,
    ingredients: [
      { id: 'ing_21', name: 'Mąka pszenna Caputo Tipo 00', unit: 'kg', packagePrice: 6.80, packageSize: 1, usedAmount: 0.220, wastePct: 2 },
      { id: 'ing_22', name: 'Pomidory San Marzano DOP', unit: 'kg', packagePrice: 16.00, packageSize: 1, usedAmount: 0.100, wastePct: 0 },
      { id: 'ing_23', name: 'Mozzarella Fior di Latte', unit: 'kg', packagePrice: 34.00, packageSize: 1, usedAmount: 0.120, wastePct: 0 },
      { id: 'ing_24', name: 'Oliwa Extra Virgin + bazylia świeża', unit: 'porcja', packagePrice: 1.50, packageSize: 1, usedAmount: 1, wastePct: 0 },
      { id: 'ing_25', name: 'Drożdże, sól morska, woda filtrowana', unit: 'porcja', packagePrice: 0.40, packageSize: 1, usedAmount: 1, wastePct: 0 },
    ],
  },
  {
    id: 'rec_3',
    name: 'Krem z Pomidorów Pieczonych z Grzanką',
    category: 'Zupy',
    servings: 1,
    targetFoodCostPct: 20,
    sellingPriceGross: 24.00,
    vatRate: 8,
    ingredients: [
      { id: 'ing_31', name: 'Pomidory pelati / malinowe', unit: 'kg', packagePrice: 11.00, packageSize: 1, usedAmount: 0.280, wastePct: 5 },
      { id: 'ing_32', name: 'Bulion warzywny domowy', unit: 'l', packagePrice: 3.50, packageSize: 1, usedAmount: 0.150, wastePct: 0 },
      { id: 'ing_33', name: 'Śmietanka 30% / Mascarpone', unit: 'kg', packagePrice: 22.00, packageSize: 1, usedAmount: 0.030, wastePct: 0 },
      { id: 'ing_34', name: 'Grzanka czosnkowa z chleba na zakwasie', unit: 'porcja', packagePrice: 1.20, packageSize: 1, usedAmount: 1, wastePct: 0 },
      { id: 'ing_35', name: 'Oliwa ziołowa i pestki dyni', unit: 'porcja', packagePrice: 0.80, packageSize: 1, usedAmount: 1, wastePct: 0 },
    ],
  },
];

// Initial HACCP and Sanepid Checklists
const INITIAL_HACCP_CHECKLIST: HaccpChecklistItem[] = [
  // 1. Odbiór Sanepidu
  {
    id: 'san_1',
    category: 'sanepid_odbior',
    title: 'Projekt technologiczny z opinią rzeczoznawcy',
    description: 'Układ technologiczny lokalu zatwierdzony przez rzeczoznawcę sanitarno-epidemiologicznego i BHP. Jasny podział stref.',
    legalBase: 'Ustawa o bezpieczeństwie żywności i żywienia',
    isCompleted: true,
  },
  {
    id: 'san_2',
    category: 'sanepid_odbior',
    title: 'Brak krzyżowania dróg czystych i brudnych',
    description: 'Dostawa surowców, obróbka wstępna (brudna), produkcja czysta, ekspedycja potraw i zwrot naczyń brudnych nie mogą na siebie nachodzić.',
    legalBase: 'Rozporządzenie (WE) nr 852/2004',
    isCompleted: true,
  },
  {
    id: 'san_3',
    category: 'sanepid_odbior',
    title: 'Dedykowane umywalki do mycia rąk i zlewy produkcyjne',
    description: 'Oddzielne stanowisko do mycia rąk (bezdotykowa bateria, mydło w płynie, ręczniki jednorazowe) oraz osobne zlewy do mycia warzyw, mięsa i sprzętu.',
    legalBase: 'Rozporządzenie (WE) nr 852/2004, Załącznik II',
    isCompleted: true,
  },
  {
    id: 'san_4',
    category: 'sanepid_odbior',
    title: 'Zaplecze socjalne i toaleta dla załogi',
    description: 'Wydzielona szatnia z szafkami dwudzielnymi (odzież prywatna i robocza) oraz toaleta dla personelu z przedsionkiem.',
    legalBase: 'Warunki techniczne dla budynków i zapleczy gastronomicznych',
    isCompleted: false,
  },
  {
    id: 'san_5',
    category: 'sanepid_odbior',
    title: 'Badanie wody (fizykochemia i mikrobiologia)',
    description: 'Aktualne sprawozdanie z badania próbek wody z kranów w lokalu pobranych przez akredytowane laboratorium.',
    legalBase: 'Rozporządzenie Ministra Zdrowia w sprawie jakości wody',
    isCompleted: false,
  },
  {
    id: 'san_6',
    category: 'sanepid_odbior',
    title: 'Umowa na odbiór odpadów i zużytego oleju (KPO)',
    description: 'Wpis do rejestru BDO, umowa na odpady komunalne oraz z wyspecjalizowaną firmą odbierającą zużyty tłuszcz smażalniczy.',
    legalBase: 'Ustawa o odpadach / Rejestr BDO',
    isCompleted: false,
  },
  {
    id: 'san_7',
    category: 'sanepid_odbior',
    title: 'Orzeczenia sanitarno-epidemiologiczne załogi',
    description: 'Wszyscy pracownicy mający kontakt z żywnością muszą posiadać aktualne orzeczenie lekarskie do celów sanitarno-epidemiologicznych.',
    legalBase: 'Ustawa o zapobieganiu oraz zwalczaniu zakażeń i chorób zakaźnych',
    isCompleted: true,
  },

  // 2. GHP / GMP & Dobre Praktyki
  {
    id: 'ghp_1',
    category: 'gmp_ghp',
    title: 'Instrukcje mycia i dezynfekcji oraz plany higieny',
    description: 'Wywieszone w kuchni i zmywaku harmonogramy sprzątania, stosowane atestowane środki chemiczne z kartami charakterystyki.',
    legalBase: 'Kodeks Praktyki Higienicznej',
    isCompleted: true,
  },
  {
    id: 'ghp_2',
    category: 'gmp_ghp',
    title: 'Zasada FIFO i etykietowanie półproduktów',
    description: 'Zasada First In, First Out. Każdy przygotowany sos, zamarynowane mięso czy bulion opisany: nazwa, data produkcji, termin przydatności i podpis kucharza.',
    legalBase: 'Dobra Praktyka Produkcyjna (GMP)',
    isCompleted: true,
  },
  {
    id: 'ghp_3',
    category: 'gmp_ghp',
    title: 'Ochrona przed szkodnikami (DDD)',
    description: 'Umowa z firmą DDD, protokół z wykonanej dezynfekcji, dezynsekcji i deratyzacji oraz mapa rozstawienia karmników deratyzacyjnych i lamp owadobójczych.',
    legalBase: 'Rozporządzenie (WE) nr 852/2004',
    isCompleted: false,
  },

  // 3. HACCP & CCP (Krytyczne Punkty Kontroli)
  {
    id: 'haccp_1',
    category: 'haccp_ccp',
    title: 'CCP 1: Kontrola temperatur w urządzeniach chłodniczych',
    description: 'Codzienny rejestr (minimum 2 razy dziennie): lodówki 0°C do +4°C, zamrażarki -18°C lub niżej. Działania korygujące w przypadku awarii.',
    legalBase: 'Zasada 2 i 3 HACCP',
    isCompleted: true,
  },
  {
    id: 'haccp_2',
    category: 'haccp_ccp',
    title: 'CCP 2: Obróbka termiczna mięs i dań (min. 75°C w środku)',
    description: 'Temperatura wewnątrz najgrubszej części potrawy musi osiągnąć min. 75°C przez co najmniej 2 minuty (lub 80°C chwilowo), sprawdzana termometrem szpilkowym.',
    legalBase: 'Norma sanitarna obróbki termicznej',
    isCompleted: true,
  },
  {
    id: 'haccp_3',
    category: 'haccp_ccp',
    title: 'CCP 3: Schładzanie ugotowanych potraw (strefa niebezpieczna)',
    description: 'Schłodzenie potrawy z 60°C do 10°C w czasie maksymalnie 2 godzin (najlepiej w schładzarce szokowej), aby zapobiec namnażaniu zarodników bakterii.',
    legalBase: 'Wymogi bezpieczeństwa mikrobiologicznego',
    isCompleted: false,
  },

  // 4. Alergeny (14 głównych alergenów UE)
  {
    id: 'al_1',
    category: 'alergeny',
    title: 'Księga alergenów i informacja dla gości w karcie',
    description: 'Wykaz 14 alergenów (gluten, skorupiaki, jaja, ryby, orzeszki ziemne, soja, mleko, orzechy, seler, gorczyca, sezam, SO2/siarczyny, łubin, mięczaki) dostępny dla każdego gościa.',
    legalBase: 'Rozporządzenie Parlamentu Europejskiego i Rady (UE) nr 1169/2011',
    isCompleted: true,
  },
  {
    id: 'al_2',
    category: 'alergeny',
    title: 'Zapobieganie zanieczyszczeniom krzyżowym alergenami',
    description: 'Osobne deski do krojenia, mycie stanowisk przed przygotowywaniem dań bezglutenowych / bezorzechowych, świadomość obsługi sali.',
    legalBase: 'Ustawa o bezpieczeństwie żywności',
    isCompleted: false,
  },
];

// Initial Menu Items (Matrix analysis)
const INITIAL_MENU_ITEMS: MenuItemAnalysis[] = [
  { id: 'm_1', name: 'Burger Wołowy Klasyk', category: 'Burgery', foodCostNet: 12.80, sellingPriceNet: 42.59, marginNet: 29.79, monthlySalesVolume: 520, classification: 'STAR' },
  { id: 'm_2', name: 'Pizza Margherita 32cm', category: 'Pizza', foodCostNet: 6.90, sellingPriceNet: 33.33, marginNet: 26.43, monthlySalesVolume: 680, classification: 'STAR' },
  { id: 'm_3', name: 'Frytki Stekowe Porcja', category: 'Dodatki', foodCostNet: 2.10, sellingPriceNet: 14.81, marginNet: 12.71, monthlySalesVolume: 820, classification: 'WORKHORSE' },
  { id: 'm_4', name: 'Stek z Polędwicy Wołowej', category: 'Dania Główne', foodCostNet: 48.00, sellingPriceNet: 110.00, marginNet: 62.00, monthlySalesVolume: 45, classification: 'PUZZLE' },
  { id: 'm_5', name: 'Zupa Rybna Bouillabaisse', category: 'Zupy', foodCostNet: 16.50, sellingPriceNet: 32.40, marginNet: 15.90, monthlySalesVolume: 35, classification: 'DOG' },
  { id: 'm_6', name: 'Krem z Pomidorów', category: 'Zupy', foodCostNet: 4.80, sellingPriceNet: 22.22, marginNet: 17.42, monthlySalesVolume: 290, classification: 'WORKHORSE' },
];

// Initial NetBeans Studio Projects (preserved from original repo)
const INITIAL_PROJECTS: ProjectEntity[] = [
  {
    id: 'proj_java',
    name: 'NetBeans Java Demo',
    description: 'Klasyczny projekt Java z klasami, algorytmem obliczeniowym i logiką biznesową',
    category: 'Java',
    createdAt: Date.now() - 86400000,
    updatedAt: Date.now() - 3600000,
  },
  {
    id: 'proj_kotlin',
    name: 'Kotlin Android Studio',
    description: 'Nowoczesny kod w języku Kotlin z korutynami, klasami danych i lambdami',
    category: 'Kotlin',
    createdAt: Date.now() - 86400000,
    updatedAt: Date.now() - 3600000,
  },
  {
    id: 'proj_web',
    name: 'Web HTML5 & JS App',
    description: 'Interaktywna aplikacja webowa z natychmiastowym podglądem w WebView',
    category: 'Web',
    createdAt: Date.now() - 86400000,
    updatedAt: Date.now() - 3600000,
  },
];

const INITIAL_PROJECT_FILES: CodeFileEntity[] = [
  {
    id: 'file_j1',
    projectId: 'proj_java',
    fileName: 'Main.java',
    fileExtension: 'java',
    isMain: true,
    updatedAt: Date.now(),
    content: `package com.example.netbeans;

/**
 * NetBeans Studio & Gastro Partner Engine
 * Obliczanie Food Cost i weryfikacja rentowności pozycji w karcie
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  NetBeans Studio Mobile & Gastro Engine ");
        System.out.println("========================================");

        double kosztSurowcowNetto = 12.80; // zł
        double cenaKartyBrutto = 46.00;   // zł brutto
        double vat = 0.08;                 // 8% VAT

        double cenaNetto = cenaKartyBrutto / (1.0 + vat);
        double foodCostPct = (kosztSurowcowNetto / cenaNetto) * 100.0;
        double marzaNetto = cenaNetto - kosztSurowcowNetto;

        System.out.printf("Cena sprzedaży netto: %.2f zł\\n", cenaNetto);
        System.out.printf("Koszt surowców: %.2f zł\\n", kosztSurowcowNetto);
        System.out.printf("Food Cost: %.2f%%\\n", foodCostPct);
        System.out.printf("Marża netto: %.2f zł\\n", marzaNetto);

        if (foodCostPct <= 30.0) {
            System.out.println("Status: EXCELLENT (Zdrowy próg rentowności < 30%)");
        } else {
            System.out.println("Status: OSTRZEŻENIE (Food Cost powyżej 30% - rozważ optymalizację)");
        }
    }
}`,
  },
  {
    id: 'file_j2',
    projectId: 'proj_java',
    fileName: 'MathCalculator.java',
    fileExtension: 'java',
    isMain: false,
    updatedAt: Date.now(),
    content: `package com.example.netbeans;

public class MathCalculator {
    public double obliczBEP(double kosztyStale, double marzaBruttoUlamkowa) {
        if (marzaBruttoUlamkowa <= 0) return 0;
        return kosztyStale / marzaBruttoUlamkowa;
    }

    public double obliczLaborCost(double obrotNetto, double wyplatyBrutto) {
        if (obrotNetto <= 0) return 0;
        return (wyplatyBrutto / obrotNetto) * 100.0;
    }
}`,
  },
  {
    id: 'file_k1',
    projectId: 'proj_kotlin',
    fileName: 'Main.kt',
    fileExtension: 'kt',
    isMain: true,
    updatedAt: Date.now(),
    content: `package com.example.kotlin

data class Dish(val name: String, val foodCostPct: Double, val volume: Int)

fun main() {
    println("🚀 Start modułu analitycznego Gastro Kotlin")
    val dishes = listOf(
        Dish("Burger Klasyk", 28.5, 520),
        Dish("Pizza Margherita", 21.0, 680),
        Dish("Stek z Polędwicy", 42.0, 45)
    )

    dishes.forEach { d ->
        println("• \${d.name}: FC=\${d.foodCostPct}%, sprzedaż=\${d.volume} porcji")
    }
}`,
  },
  {
    id: 'file_w1',
    projectId: 'proj_web',
    fileName: 'index.html',
    fileExtension: 'html',
    isMain: true,
    updatedAt: Date.now(),
    content: `<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title>Gastro Live Runner</title>
    <style>
        body { font-family: sans-serif; background: #0f172a; color: #f8fafc; padding: 24px; text-align: center; }
        .card { background: #1e293b; padding: 20px; border-radius: 16px; max-width: 380px; margin: auto; border: 1px solid #334155; }
        h1 { color: #38bdf8; font-size: 20px; }
        .val { font-size: 32px; font-weight: bold; color: #34d399; margin: 16px 0; }
        button { background: #2563eb; color: white; border: none; padding: 10px 18px; border-radius: 10px; cursor: pointer; font-weight: bold; }
    </style>
</head>
<body>
    <div class="card">
        <h1>📊 Symulator Rachunków Dziennych</h1>
        <p>Liczba obsłużonych gości dzisiaj:</p>
        <div class="val" id="guests">48</div>
        <button onclick="addGuest()">+ Dodaj gościa (+65 zł)</button>
        <p style="margin-top: 15px; font-size: 13px; color: #94a3b8;">Estymowany obrót: <span id="rev" style="color: #60a5fa; font-weight: bold;">3 120 zł</span></p>
    </div>
    <script>
        let g = 48;
        function addGuest() {
            g++;
            document.getElementById('guests').innerText = g;
            document.getElementById('rev').innerText = (g * 65).toLocaleString() + ' zł';
        }
    </script>
</body>
</html>`,
  },
];

// Helper methods for localStorage
export const db = {
  // Chat
  getMessages(): ChatMessage[] {
    const raw = localStorage.getItem(STORAGE_KEYS.CHAT_MESSAGES);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.CHAT_MESSAGES, JSON.stringify(INITIAL_CHAT_MESSAGES));
      return INITIAL_CHAT_MESSAGES;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_CHAT_MESSAGES;
    }
  },
  saveMessage(msg: ChatMessage) {
    const messages = this.getMessages();
    messages.push(msg);
    localStorage.setItem(STORAGE_KEYS.CHAT_MESSAGES, JSON.stringify(messages));
    return messages;
  },
  clearMessages() {
    localStorage.setItem(STORAGE_KEYS.CHAT_MESSAGES, JSON.stringify(INITIAL_CHAT_MESSAGES));
    return INITIAL_CHAT_MESSAGES;
  },

  // Recipes
  getRecipes(): Recipe[] {
    const raw = localStorage.getItem(STORAGE_KEYS.RECIPES);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.RECIPES, JSON.stringify(INITIAL_RECIPES));
      return INITIAL_RECIPES;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_RECIPES;
    }
  },
  saveRecipe(recipe: Recipe) {
    const recipes = this.getRecipes();
    const idx = recipes.findIndex(r => r.id === recipe.id);
    if (idx >= 0) {
      recipes[idx] = recipe;
    } else {
      recipes.push(recipe);
    }
    localStorage.setItem(STORAGE_KEYS.RECIPES, JSON.stringify(recipes));
    return recipes;
  },
  deleteRecipe(id: string) {
    const recipes = this.getRecipes().filter(r => r.id !== id);
    localStorage.setItem(STORAGE_KEYS.RECIPES, JSON.stringify(recipes));
    return recipes;
  },

  // HACCP Checklist
  getHaccpChecklist(): HaccpChecklistItem[] {
    const raw = localStorage.getItem(STORAGE_KEYS.HACCP_CHECKLIST);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.HACCP_CHECKLIST, JSON.stringify(INITIAL_HACCP_CHECKLIST));
      return INITIAL_HACCP_CHECKLIST;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_HACCP_CHECKLIST;
    }
  },
  toggleHaccpItem(id: string) {
    const items = this.getHaccpChecklist();
    const item = items.find(i => i.id === id);
    if (item) {
      item.isCompleted = !item.isCompleted;
      localStorage.setItem(STORAGE_KEYS.HACCP_CHECKLIST, JSON.stringify(items));
    }
    return items;
  },

  // Menu items
  getMenuItems(): MenuItemAnalysis[] {
    const raw = localStorage.getItem(STORAGE_KEYS.MENU_ITEMS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.MENU_ITEMS, JSON.stringify(INITIAL_MENU_ITEMS));
      return INITIAL_MENU_ITEMS;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_MENU_ITEMS;
    }
  },
  saveMenuItem(item: MenuItemAnalysis) {
    const items = this.getMenuItems();
    const idx = items.findIndex(m => m.id === item.id);
    if (idx >= 0) {
      items[idx] = item;
    } else {
      items.push(item);
    }
    localStorage.setItem(STORAGE_KEYS.MENU_ITEMS, JSON.stringify(items));
    return items;
  },
  deleteMenuItem(id: string) {
    const items = this.getMenuItems().filter(m => m.id !== id);
    localStorage.setItem(STORAGE_KEYS.MENU_ITEMS, JSON.stringify(items));
    return items;
  },

  // Projects & Files
  getProjects(): ProjectEntity[] {
    const raw = localStorage.getItem(STORAGE_KEYS.PROJECTS);
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.PROJECTS, JSON.stringify(INITIAL_PROJECTS));
      return INITIAL_PROJECTS;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_PROJECTS;
    }
  },
  getProjectFiles(projectId: string): CodeFileEntity[] {
    const raw = localStorage.getItem(STORAGE_KEYS.PROJECT_FILES);
    const allFiles: CodeFileEntity[] = raw ? JSON.parse(raw) : INITIAL_PROJECT_FILES;
    if (!raw) {
      localStorage.setItem(STORAGE_KEYS.PROJECT_FILES, JSON.stringify(INITIAL_PROJECT_FILES));
    }
    return allFiles.filter(f => f.projectId === projectId);
  },
  saveFileContent(fileId: string, newContent: string) {
    const raw = localStorage.getItem(STORAGE_KEYS.PROJECT_FILES);
    const allFiles: CodeFileEntity[] = raw ? JSON.parse(raw) : INITIAL_PROJECT_FILES;
    const file = allFiles.find(f => f.id === fileId);
    if (file) {
      file.content = newContent;
      file.updatedAt = Date.now();
      localStorage.setItem(STORAGE_KEYS.PROJECT_FILES, JSON.stringify(allFiles));
    }
    return allFiles;
  },
  addProject(name: string, description: string, category: string): ProjectEntity {
    const projects = this.getProjects();
    const newProj: ProjectEntity = {
      id: 'proj_' + Date.now(),
      name,
      description,
      category,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };
    projects.push(newProj);
    localStorage.setItem(STORAGE_KEYS.PROJECTS, JSON.stringify(projects));

    // Create default file
    const rawFiles = localStorage.getItem(STORAGE_KEYS.PROJECT_FILES);
    const files: CodeFileEntity[] = rawFiles ? JSON.parse(rawFiles) : INITIAL_PROJECT_FILES;
    files.push({
      id: 'file_' + Date.now(),
      projectId: newProj.id,
      fileName: category.toLowerCase() === 'java' ? 'Main.java' : category.toLowerCase() === 'web' ? 'index.html' : 'main.kt',
      fileExtension: category.toLowerCase() === 'java' ? 'java' : category.toLowerCase() === 'web' ? 'html' : 'kt',
      content: `// Projekt: ${name}\n// Kategoria: ${category}\n`,
      isMain: true,
      updatedAt: Date.now(),
    });
    localStorage.setItem(STORAGE_KEYS.PROJECT_FILES, JSON.stringify(files));
    return newProj;
  },
  addFile(projectId: string, fileName: string, fileExtension: string, content = ''): CodeFileEntity {
    const rawFiles = localStorage.getItem(STORAGE_KEYS.PROJECT_FILES);
    const files: CodeFileEntity[] = rawFiles ? JSON.parse(rawFiles) : INITIAL_PROJECT_FILES;
    const newFile: CodeFileEntity = {
      id: 'file_' + Date.now(),
      projectId,
      fileName,
      fileExtension,
      content: content || `// Plik: ${fileName}\n`,
      isMain: false,
      updatedAt: Date.now(),
    };
    files.push(newFile);
    localStorage.setItem(STORAGE_KEYS.PROJECT_FILES, JSON.stringify(files));
    return newFile;
  },

  // API Key
  getCustomApiKey(): string {
    return localStorage.getItem(STORAGE_KEYS.CUSTOM_API_KEY) || '';
  },
  setCustomApiKey(key: string) {
    localStorage.setItem(STORAGE_KEYS.CUSTOM_API_KEY, key.trim());
  },
};
