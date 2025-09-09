// Base Response Type
export interface BaseResponse<T> {
  status: string;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  page: number;
  size: number;
  content: T[];
}

// Enums
export enum ActiveStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
}

export enum RoastLevel {
  LIGHT = 'LIGHT',
  MEDIUM_LIGHT = 'MEDIUM_LIGHT',
  MEDIUM = 'MEDIUM',
  MEDIUM_DARK = 'MEDIUM_DARK',
  DARK = 'DARK',
}

export enum ProcessType {
  WASHED = 'WASHED',
  NATURAL = 'NATURAL',
  HONEY = 'HONEY',
  WET_HULLED = 'WET_HULLED',
  ANAEROBIC = 'ANAEROBIC',
}

export enum BlendType {
  SINGLE_ORIGIN = 'SINGLE_ORIGIN',
  BLEND = 'BLEND',
}

export enum CategoryType {
  HAND_DRIP = 'HAND_DRIP',
  ESPRESSO = 'ESPRESSO',
  COLD_BREW = 'COLD_BREW',
  MOCHA_POT = 'MOCHA_POT',
}

export enum EquipmentType {
  GRINDER = 'GRINDER',
  DRIPPER = 'DRIPPER',
  MACHINE = 'MACHINE',
  SCALE = 'SCALE',
  KETTLE = 'KETTLE',
  ACCESSORY = 'ACCESSORY',
}

// Value Objects
export interface Origin {
  country: string;
  region?: string;
  farm?: string;
}

export interface FlavorResponse {
  id: number;
  name: string;
  description?: string;
}

// Bean Types
export interface BeanResponse {
  beanId: number;
  name: string;
  thumbnailUrl?: string;
  origin: Origin;
  roaster: string;
  roastDate: string; // ISO date string
  grams: number;
  roastLevel: RoastLevel;
  processType: ProcessType;
  blendType: BlendType;
  isDecaf: boolean;
  flavors: FlavorResponse[];
  memo?: string;
  status: ActiveStatus;
  createdAt: string; // ISO datetime string
  updatedAt: string; // ISO datetime string
}

export interface CreateBeanCommand {
  name: string;
  thumbnailUrl?: string;
  origin: Origin;
  roaster: string;
  roastDate: string;
  grams: number;
  roastLevel: RoastLevel;
  processType: ProcessType;
  blendType: BlendType;
  isDecaf: boolean;
  flavorIds?: number[];
  memo?: string;
  status: ActiveStatus;
}

export interface UpdateBeanCommand extends CreateBeanCommand {
  id?: number;
}

// Recipe Types
export interface Ingredient {
  name: string;
  amount: number;
  unit: string;
  buyUrl?: string;
}

export interface RecipeStep {
  imageUrl?: string;
  description: string;
}

export interface RecipeResponse {
  id: number;
  userId: number;
  category: CategoryType;
  title: string;
  thumbnailUrl?: string;
  description?: string;
  serving: number;
  tags: string[];
  ingredients: Ingredient[];
  steps: RecipeStep[];
  tips?: string;
  status: ActiveStatus;
  createdAt: string;
  updatedAt: string;
}

export interface RecipeSummaryResponse {
  id: number;
  userId: number;
  category: CategoryType;
  title: string;
  thumbnailUrl?: string;
  description?: string;
  serving: number;
  tags: string[];
  status: ActiveStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRecipeCommand {
  category: CategoryType;
  title: string;
  thumbnailUrl?: string;
  description?: string;
  serving: number;
  tags: string[];
  ingredients: CreateIngredientCommand[];
  steps: CreateStepCommand[];
  tips?: string;
}

export interface CreateIngredientCommand {
  name: string;
  amount: number;
  unit: string;
  buyUrl?: string;
}

export interface CreateStepCommand {
  imageUrl?: string;
  description: string;
}

export interface UpdateRecipeCommand {
  category: CategoryType;
  title: string;
  thumbnailUrl?: string;
  description?: string;
  serving: number;
  tags: string[];
  ingredients: CreateIngredientCommand[];
  steps: CreateStepCommand[];
  tips?: string;
}

// Equipment Types
export interface EquipmentResponse {
  id: number;
  type: EquipmentType;
  name: string;
  brand: string;
  status: ActiveStatus;
  description?: string;
  buyDate?: string; // ISO date string
  buyUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEquipmentCommand {
  type: EquipmentType;
  name: string;
  brand: string;
  description?: string;
  buyDate?: string;
  buyUrl?: string;
}

export interface UpdateEquipmentCommand {
  type: EquipmentType;
  name: string;
  brand: string;
  description?: string;
  buyDate?: string;
  buyUrl?: string;
}

export interface DeleteEquipmentResponse {
  id: number;
  message: string;
}

// Search Query Types
export interface BeanSearchQuery {
  page?: number;
  size?: number;
  sort?: string;
}

export interface RecipeSearchQuery {
  page?: number;
  size?: number;
  sort?: string;
}

export interface EquipmentSearchQuery {
  page?: number;
  size?: number;
  sort?: string;
}
