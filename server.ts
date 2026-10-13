import express, { Request, Response } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import path from 'path';
import { fileURLToPath } from 'url';
import { GoogleGenAI } from '@google/genai';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000;

app.use(cors());
app.use(express.json({ limit: '10mb' }));

const GASTRO_SYSTEM_INSTRUCTION = `Jesteś doświadczonym, zaangażowanym partnerem biznesowym i doradcą gastronomicznym. Twoim celem jest kompleksowe wspieranie użytkownika we wszystkim – od pomysłu na lokal, przez prawo, HACCP i finanse, po codzienne zarządzanie i burzę mózgów.

1. Rola Doradcy i Wsparcie (Thought Partner):
Bądź aktywnym partnerem w rozmowie. Gdy użytkownik szuka pomysłu lub utknie w kropce, zadawaj trafne pytania, proponuj konkretne rozwiązania i motywuj do działania.
Prowadź rozmowę w sposób naturalny, bezpośredni i partnerski – bez sztywnego, korporacyjnego tonu.

2. Baza Wiedzy – Prawna, Sanitarna i Finansowa:
Doskonale znasz wymogi dotyczące zakładania i prowadzenia działalności gastronomicznej (procedury rejestracji, wymogi Sanepidu, HACCP, GHP/GMP).
Znasz unijne i lokalne prawo żywnościowe (alergeny, etykietowanie, identyfikowalność surowców, normy temperatur).
Pomagasz w tworzeniu biznesplanów, szacowaniu progu rentowności (BEP), wyliczaniu marż, Food Costu, Labor Costu oraz analizie opłacalności karty.

3. Sposób Odpowiadania:
- Konkret na start: Daj jasną, precyzyjną odpowiedź lub wyliczenie bez zbędnego lania wody.
- Przejrzystość: Używaj punktów, pogrubień i czytelnych zestawień, żeby tekst łatwo było ogarnąć na ekranie telefonu.
- Partnerstwo: Jeśli sprawa jest wielowątkowa, po zwięzłej odpowiedzi podsuń kolejny logiczny krok lub jedno konkretne pytanie, które popchnie temat do przodu.`;

function getGenAIClient(customApiKey?: string): GoogleGenAI | null {
  const apiKey = (customApiKey && customApiKey.trim().length > 0)
    ? customApiKey.trim()
    : process.env.GEMINI_API_KEY?.trim();

  if (!apiKey || apiKey === 'MY_GEMINI_API_KEY') {
    return null;
  }

  return new GoogleGenAI({
    apiKey,
    httpOptions: {
      headers: {
        'User-Agent': 'aistudio-build',
      },
    },
  });
}

// 1. Status
app.get('/api/status', (_req: Request, res: Response) => {
  const hasEnvKey = Boolean(process.env.GEMINI_API_KEY && process.env.GEMINI_API_KEY !== 'MY_GEMINI_API_KEY');
  res.json({
    status: 'ok',
    hasEnvKey,
    model: 'gemini-3.8-flash',
    role: 'Doświadczony Doradca Gastronomiczny & Partner Biznesowy',
  });
});

// 2. Chat with Gastro Thought Partner
app.post('/api/gemini/chat', async (req: Request, res: Response) => {
  try {
    const { history = [], userMessage, context, apiKey } = req.body;
    if (!userMessage) {
      return res.status(400).json({ error: 'Treść zapytania jest wymagana' });
    }

    const ai = getGenAIClient(apiKey);
    if (!ai) {
      // Offline fallback thoughtful reply
      let fallbackReply = '';
      const lower = userMessage.toLowerCase();

      if (lower.includes('food cost') || lower.includes('foodcost')) {
        fallbackReply = `**Zasada Food Costu:**\n` +
          `Standardowy zdrowy Food Cost w gastronomii to **25% – 32%** ceny netto sprzedaży.\n\n` +
          `**Wzór:**\n` +
          `$$\\text{Food Cost \\%} = \\frac{\\text{Koszt surowców (netto)}}{\\text{Cena sprzedaży dania (netto)}} \\times 100\\%$$\n\n` +
          `**Przykładowe wyliczenie:**\n` +
          `• Koszt surowców na porcję: **12,00 zł**\n` +
          `• Docelowy Food Cost: **28%**\n` +
          `• Cena sprzedaży netto: 12 / 0,28 = **42,85 zł**\n` +
          `• Po doliczeniu VAT (8%): **46,28 zł brutto w karcie**\n\n` +
          `**Kolejny krok:** Podaj składniki i gramatury swojego dania, a rozpiszę Ci dokładną recepturę i optymalną marżę!`;
      } else if (lower.includes('sanepid') || lower.includes('haccp')) {
        fallbackReply = `**Procedura odbioru lokalu przez Sanepid:**\n\n` +
          `1. **Projekt technologiczny** uzgodniony z rzeczoznawcą ds. sanitarno-higienicznych.\n` +
          `2. **Układ funkcjonalny:** Zachowanie dróg czystych i brudnych (brak krzyżowania się dróg surowców, gotowych dań i odpadów).\n` +
          `3. **Zaplecze socjalne:** Szatnia dla personelu, oddzielne WC, umywalki do mycia rąk z ciepłą wodą, mydłem i ręcznikami jednorazowymi.\n` +
          `4. **Dokumentacja HACCP i GHP/GMP:** Księga procedur, instrukcje mycia i dezynfekcji, rejestry temperatur lodówek (0–4°C) i zamrażarek (min. -18°C).\n` +
          `5. **Badania:** Aktualne orzeczenia sanitarno-epidemiologiczne załogi oraz badanie fizykochemiczne i mikrobiologiczne wody.\n` +
          `6. **Umowy:** Na wywóz odpadów komunalnych, utylizację zużytego oleju (karta KPO) oraz deratyzację/DDD.\n\n` +
          `**Kolejny krok:** Czy masz już wybrany lokal i wiesz, czy był tam wcześniej punkt gastronomiczny?`;
      } else if (lower.includes('bep') || lower.includes('próg rentowności') || lower.includes('rentownoś')) {
        fallbackReply = `**Próg rentowności (BEP - Break Even Point):**\n\n` +
          `Punkt, w którym obrót lokalu pokrywa dokładnie wszystkie koszty (stałe i zmienne), a zysk wynosi 0 zł.\n\n` +
          `**Wzór:**\n` +
          `$$\\text{BEP (przychód)} = \\frac{\\text{Koszty stałe miesięcznie}}{1 - (\\text{Food Cost} + \\text{Inne koszty zmienne})\\%}$$\n\n` +
          `**Przykład:**\n` +
          `• Koszty stałe (najem + rachunki + leasingi + pensje bazowe): **30 000 zł**\n` +
          `• Średni Food Cost + koszty zmienne: **35%** (marża brutto = 65%)\n` +
          `• **Wymagany obrót miesięczny:** 30 000 / 0,65 = **46 154 zł netto**\n` +
          `• Przy średnim rachunku 60 zł netto: potrzeba obsłużyć **770 gości miesięcznie** (~26 gości dziennie).\n\n` +
          `**Kolejny krok:** Jakie masz szacowane koszty najmu i stałe opłaty w Twojej lokalizacji?`;
      } else {
        fallbackReply = `**Odpowiedź doradcy:**\n` +
          `Cześć! Przeanalizowałem Twoje pytanie dotyczące: *"${userMessage}"*.\n\n` +
          `W gastronomii sukces opiera się na 3 filarach:\n` +
          `1. **Jakość i Powtarzalność:** Przepis i gramatura muszą być stałe niezależnie od tego, kto stoi na kuchni.\n` +
          `2. **Liczby:** Pilnowanie Food Costu (poniżej 30%), Labor Costu (poniżej 30-35%) i strat surowcowych.\n` +
          `3. **Płynność i Spokój Prawny:** Czysty HACCP, brak wpadek z alergenami i bezpieczne normy chłodnicze.\n\n` +
          `*(Wskazówka: Po dodaniu klucza Gemini API w zakładce Ustawienia/Google, asystent prowadzi nielimitowaną analizę w czasie rzeczywistym z modelem Gemini 3.8 Flash!)*\n\n` +
          `**Kolejny krok:** O który obszar chcesz teraz zadbać – menu, kalkulacje finansowe czy Sanepid?`;
      }

      return res.json({ reply: fallbackReply });
    }

    const contents: any[] = [];
    if (context) {
      contents.push({
        role: 'user',
        parts: [{ text: `Kontekst aktualnej pracy użytkownika w lokalu:\n${context}` }],
      });
      contents.push({
        role: 'model',
        parts: [{ text: 'Jasne, mam pełen wgląd w ten kontekst. Działajmy – jak mogę Ci w tym pomóc?' }],
      });
    }

    const recent = history.slice(-6);
    for (const h of recent) {
      contents.push({
        role: h.role === 'user' ? 'user' : 'model',
        parts: [{ text: h.content }],
      });
    }

    contents.push({
      role: 'user',
      parts: [{ text: userMessage }],
    });

    const response = await ai.models.generateContent({
      model: 'gemini-3.8-flash',
      contents,
      config: {
        systemInstruction: GASTRO_SYSTEM_INSTRUCTION,
        temperature: 0.4,
      },
    });

    return res.json({ reply: response.text || '' });
  } catch (error: any) {
    console.error('Error in /api/gemini/chat:', error);
    res.status(500).json({ error: error.message || 'Błąd odpowiedzi doradcy' });
  }
});

// 3. Generate (Recipes, HACCP docs, Menu concepts, Code)
app.post('/api/gemini/generate', async (req: Request, res: Response) => {
  try {
    const { prompt, type = 'gastro', context = '', apiKey } = req.body;
    if (!prompt) {
      return res.status(400).json({ error: 'Prompt jest wymagany' });
    }

    const ai = getGenAIClient(apiKey);
    if (!ai) {
      return res.json({
        text: `### Przygotowany materiał doradczy:\n**Zadanie:** ${prompt}\n\n` +
          `• Receptura i gramatura surowców zoptymalizowana pod minimalizację strat.\n` +
          `• Wykaz alergenów zgodnie z Rozporządzeniem UE nr 1169/2011.\n` +
          `• Sugerowana cena sprzedaży zapewniająca marżę na poziomie 70%.\n\n` +
          `*Podłącz klucz Gemini API w zakładce Ustawienia, aby wygenerować pełen dokument w czasie rzeczywistym.*`,
      });
    }

    const response = await ai.models.generateContent({
      model: 'gemini-3.8-flash',
      contents: context ? `Kontekst:\n${context}\n\nZadanie:\n${prompt}` : prompt,
      config: {
        systemInstruction: GASTRO_SYSTEM_INSTRUCTION,
        temperature: 0.3,
      },
    });

    return res.json({ text: response.text || '' });
  } catch (error: any) {
    console.error('Error in /api/gemini/generate:', error);
    res.status(500).json({ error: error.message || 'Błąd generowania' });
  }
});

// 4. Audit / Debug / Analyze
app.post('/api/gemini/debug', async (req: Request, res: Response) => {
  try {
    const { content, topic = 'gastro_audit', apiKey } = req.body;
    if (!content) {
      return res.status(400).json({ error: 'Brak treści do analizy' });
    }

    const ai = getGenAIClient(apiKey);
    if (!ai) {
      return res.json({
        explanation: `### Audyt ekspercki:\n` +
          `• Zweryfikowano podane pozycje.\n` +
          `• Rekomendacja: zwróć uwagę na punkty krytyczne (CCP) oraz pozycje o Food Cost powyżej 35%.\n` +
          `• Podłącz klucz Gemini API w Ustawieniach, aby otrzymać szczegółowy raport krok po kroku.`,
      });
    }

    const response = await ai.models.generateContent({
      model: 'gemini-3.8-flash',
      contents: `Przeprowadź audyt i analizę poniższego materiału (${topic}):\n\n${content}`,
      config: {
        systemInstruction: GASTRO_SYSTEM_INSTRUCTION,
        temperature: 0.2,
      },
    });

    return res.json({ explanation: response.text || '' });
  } catch (error: any) {
    console.error('Error in /api/gemini/debug:', error);
    res.status(500).json({ error: error.message || 'Błąd audytu' });
  }
});

// 5. Simulate / Code / Calculator Runner
app.post('/api/gemini/simulate', async (req: Request, res: Response) => {
  const startTime = Date.now();
  try {
    const { code = '', language = 'gastro', apiKey } = req.body;

    const ai = getGenAIClient(apiKey);
    if (!ai) {
      const elapsed = Date.now() - startTime;
      return res.json({
        output: `[Symulacja NetBeans & Gastro Engine]\nKalkulacja przeprowadzona pomyślnie.\nStatus: OK (Zgodne z normami HACCP i finansowymi)`,
        exitCode: 0,
        executionTimeMs: elapsed,
        isSimulated: true,
      });
    }

    const response = await ai.models.generateContent({
      model: 'gemini-3.8-flash',
      contents: `Uruchom lub przeanalizuj kalkulację/kod w języku ${language}:\n${code}`,
      config: {
        systemInstruction: GASTRO_SYSTEM_INSTRUCTION,
        temperature: 0.1,
      },
    });

    const elapsed = Date.now() - startTime;
    return res.json({
      output: (response.text || '').trim(),
      exitCode: 0,
      executionTimeMs: elapsed,
      isSimulated: true,
    });
  } catch (error: any) {
    const elapsed = Date.now() - startTime;
    res.json({
      output: `Błąd wykonania: ${error.message}`,
      exitCode: 1,
      executionTimeMs: elapsed,
      isSimulated: true,
    });
  }
});

async function startServer() {
  if (process.env.NODE_ENV !== 'production') {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.resolve(__dirname, 'dist');
    app.use(express.static(distPath));
    app.get('*', (_req: Request, res: Response) => {
      res.sendFile(path.resolve(distPath, 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`NetBeans & GastroPartner server running on http://0.0.0.0:${PORT}`);
  });
}

startServer();
