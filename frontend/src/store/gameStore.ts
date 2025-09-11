import { create } from "zustand";

export type CountryStats = {
  eco: number;
  mil: number;
  opi: number;
  env: number;
};

export type RelatedArticle = {
  title: string;
  url: string;
};

type GameState = {
  gameId: string | null;
  currentTurn: number;
  currentStats: CountryStats;
  countryName: string;
  playerName: string;
  setGameStart: (
    gameId: string, 
    stats: CountryStats,
    countryName: string,
    playerName: string,
    turn: number,
  ) => void;
  setStats: (stats: CountryStats) => void;
  setTurn: (turn: number) => void;
  resetGame: () => void;
};

export const useGameStore = create<GameState>((set) => ({
  gameId: null,
  currentTurn: 1,
  currentStats: { eco: 50, mil: 50, opi: 50, env: 50 },
  countryName: "",
  playerName: "",
  setGameStart: (gameId, stats, countryName, playerName, turn) =>
    set({
      gameId,
      currentStats: stats,
      currentTurn: turn,
      countryName,
      playerName,
  }),
  setStats: (stats) => set({ currentStats: stats }),
  setTurn: (turn) => set({currentTurn: turn}),
  resetGame: () =>
    set({
      gameId: null,
      currentTurn: 1,
      currentStats: { eco: 50, mil: 50, opi: 50, env: 50 },
      countryName: "",
      playerName: "",
    }),
}));
