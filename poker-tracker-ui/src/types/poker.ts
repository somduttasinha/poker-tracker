export interface Game {
    id: string;
    name: string;
    dateCreated: string;
}

export interface Player {
    id: string;
    name: string;
    game: Game;
}

export interface BuyIn {
    id: string;
    amount: number;
    timestamp: string;
    player: Player;
}

export interface Stack {
    id: string;
    amount: number;
    player: Player;
}
