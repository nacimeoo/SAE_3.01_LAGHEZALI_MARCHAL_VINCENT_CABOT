-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : ven. 09 jan. 2026 à 17:48
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `sae_taches`
--

-- --------------------------------------------------------

--
-- Structure de la table `colonne`
--

CREATE TABLE `colonne` (
  `id` int(11) NOT NULL,
  `titre` text NOT NULL,
  `position` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `colonne`
--

INSERT INTO `colonne` (`id`, `titre`, `position`) VALUES
(49, 'A faire', NULL),
(50, 'En cours', NULL),
(51, 'Terminé', NULL),
(52, 'O', NULL),
(53, 'P', NULL),
(54, 'Q', NULL),
(55, 'R', NULL),
(56, 'T', NULL),
(57, 'X', NULL),
(58, 'Y', NULL),
(59, 'Z', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `colonne2tache`
--

CREATE TABLE `colonne2tache` (
  `id_colonne` int(11) NOT NULL,
  `id_tache` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `colonne2tache`
--

INSERT INTO `colonne2tache` (`id_colonne`, `id_tache`) VALUES
(49, 179),
(49, 186),
(50, 184),
(51, 187),
(52, 188),
(52, 189),
(52, 190),
(52, 212),
(53, 191),
(53, 192),
(53, 193),
(53, 218),
(54, 194),
(54, 195),
(54, 196),
(54, 219),
(55, 197),
(55, 198),
(55, 199),
(55, 220),
(56, 200),
(56, 201),
(56, 202),
(57, 203),
(57, 204),
(57, 205),
(58, 206),
(58, 207),
(58, 208),
(59, 209),
(59, 210),
(59, 211);

-- --------------------------------------------------------

--
-- Structure de la table `dependance`
--

CREATE TABLE `dependance` (
  `id_tache_mere` int(11) NOT NULL,
  `id_sous_tache` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `dependance`
--

INSERT INTO `dependance` (`id_tache_mere`, `id_sous_tache`) VALUES
(179, 182),
(179, 183),
(181, 180),
(184, 181),
(184, 185),
(212, 213),
(212, 214),
(218, 215),
(218, 216),
(218, 217),
(220, 221);

-- --------------------------------------------------------

--
-- Structure de la table `etiquette`
--

CREATE TABLE `etiquette` (
  `id` int(11) NOT NULL,
  `nom` text NOT NULL,
  `couleur` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `etiquette`
--

INSERT INTO `etiquette` (`id`, `nom`, `couleur`) VALUES
(36, 'Conception', '#6680e6ff'),
(37, 'Analyse', '#ff8080ff'),
(38, 'Base de données', '#b3b31aff');

-- --------------------------------------------------------

--
-- Structure de la table `projet`
--

CREATE TABLE `projet` (
  `id` int(11) NOT NULL,
  `nom` text NOT NULL,
  `dateCreation` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `projet`
--

INSERT INTO `projet` (`id`, `nom`, `dateCreation`) VALUES
(21, 'Projet de Test', '2026-01-09'),
(23, 'Projet Exemple Arche', '2026-01-09');

-- --------------------------------------------------------

--
-- Structure de la table `projet2colonne`
--

CREATE TABLE `projet2colonne` (
  `id_projet` int(11) NOT NULL,
  `id_colonne` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `projet2colonne`
--

INSERT INTO `projet2colonne` (`id_projet`, `id_colonne`) VALUES
(21, 49),
(21, 50),
(21, 51),
(23, 52),
(23, 53),
(23, 54),
(23, 55),
(23, 56),
(23, 57),
(23, 58),
(23, 59);

-- --------------------------------------------------------

--
-- Structure de la table `tache`
--

CREATE TABLE `tache` (
  `id` int(11) NOT NULL,
  `titre` text NOT NULL,
  `description` text DEFAULT NULL,
  `priorite` int(11) DEFAULT NULL,
  `DateDebut` date DEFAULT NULL,
  `duree` int(11) DEFAULT NULL,
  `etat` text DEFAULT NULL,
  `type` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `tache`
--

INSERT INTO `tache` (`id`, `titre`, `description`, `priorite`, `DateDebut`, `duree`, `etat`, `type`) VALUES
(179, 'Conception diagramme de classe', NULL, 3, '2026-01-13', 4, 'A faire', 0),
(180, 'Liste cas d\'utilisations', NULL, 2, '2026-01-10', 2, 'A faire', 0),
(181, 'Diagramme de cas d\'utilisation', 'A faire après la liste des cas d\'utilisation', 2, '2026-01-12', 2, 'A faire', 0),
(182, 'Choix des patrons', 'Patrons : Decorateur - Composite', 3, '2026-01-11', 2, 'En cours', 0),
(183, 'Création BD', NULL, 2, '2026-01-11', 2, 'Terminer', 0),
(184, 'Analyse', NULL, 2, '2026-01-14', 2, 'A faire', 0),
(185, 'Diagramme d\'état', NULL, 2, '2026-01-12', 2, 'A faire', 0),
(186, 'Planning itération', 'Description du planning d\'itération', 2, '2026-01-17', 3, 'A faire', 0),
(187, 'Tache Archivée', NULL, 2, '2026-01-09', 1, 'Archivée', 0),
(188, 'O1', NULL, 0, NULL, 0, 'A faire', 0),
(189, 'O2', NULL, 0, NULL, 0, 'A faire', 0),
(190, 'O3', NULL, 0, NULL, 0, 'A faire', 0),
(191, 'P1', NULL, 0, NULL, 0, 'A faire', 0),
(192, 'p2', NULL, 0, NULL, 0, 'A faire', 0),
(193, 'P3', NULL, 0, NULL, 0, 'A faire', 0),
(194, 'Q1', NULL, 0, NULL, 0, 'A faire', 0),
(195, 'Q2', NULL, 0, NULL, 0, 'A faire', 0),
(196, 'Q3', NULL, 0, NULL, 0, 'A faire', 0),
(197, 'R1', NULL, 0, NULL, 0, 'A faire', 0),
(198, 'R2', NULL, 0, NULL, 0, 'A faire', 0),
(199, 'R3', NULL, 0, NULL, 0, 'A faire', 0),
(200, 'T1', NULL, 0, NULL, 0, 'A faire', 0),
(201, 'T2', NULL, 0, NULL, 0, 'A faire', 0),
(202, 'T3', NULL, 0, NULL, 0, 'A faire', 0),
(203, 'X1', NULL, 0, NULL, 0, 'A faire', 0),
(204, 'X2', NULL, 0, NULL, 0, 'A faire', 0),
(205, 'X3', NULL, 0, NULL, 0, 'A faire', 0),
(206, 'Y1', NULL, 0, NULL, 0, 'A faire', 0),
(207, 'Y2', NULL, 0, NULL, 0, 'A faire', 0),
(208, 'Y3', NULL, 0, NULL, 0, 'A faire', 0),
(209, 'Z1', NULL, 0, NULL, 0, 'A faire', 0),
(210, 'Z2', NULL, 0, NULL, 0, 'A faire', 0),
(211, 'Z3', NULL, 0, NULL, 0, 'A faire', 0),
(212, 'A', NULL, 2, '2026-02-25', 7, 'A faire', 0),
(213, 'A1', NULL, 2, '2026-02-18', 7, 'A faire', 0),
(214, 'A2', NULL, 0, NULL, 0, 'A faire', 0),
(215, 'B1', NULL, 2, '2026-02-13', 14, 'A faire', 0),
(216, 'B2', NULL, 0, NULL, 0, 'A faire', 0),
(217, 'B3', NULL, 2, '2026-02-05', 21, 'A faire', 0),
(218, 'B', NULL, 2, '2026-02-27', 35, 'A faire', 0),
(219, 'C', NULL, 0, NULL, 0, 'A faire', 0),
(220, 'D', NULL, 0, NULL, 0, 'A faire', 0),
(221, 'D1', NULL, 0, NULL, 0, 'A faire', 0);

-- --------------------------------------------------------

--
-- Structure de la table `tache2etiquette`
--

CREATE TABLE `tache2etiquette` (
  `id_tache` int(11) NOT NULL,
  `id_etiquette` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `tache2etiquette`
--

INSERT INTO `tache2etiquette` (`id_tache`, `id_etiquette`) VALUES
(179, 36),
(183, 38),
(184, 37),
(186, 37);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `colonne`
--
ALTER TABLE `colonne`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `colonne2tache`
--
ALTER TABLE `colonne2tache`
  ADD PRIMARY KEY (`id_colonne`,`id_tache`),
  ADD KEY `id_tache` (`id_tache`);

--
-- Index pour la table `dependance`
--
ALTER TABLE `dependance`
  ADD PRIMARY KEY (`id_tache_mere`,`id_sous_tache`),
  ADD KEY `id_sous_tache` (`id_sous_tache`);

--
-- Index pour la table `etiquette`
--
ALTER TABLE `etiquette`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `projet`
--
ALTER TABLE `projet`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `projet2colonne`
--
ALTER TABLE `projet2colonne`
  ADD PRIMARY KEY (`id_projet`,`id_colonne`),
  ADD KEY `id_colonne` (`id_colonne`);

--
-- Index pour la table `tache`
--
ALTER TABLE `tache`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `tache2etiquette`
--
ALTER TABLE `tache2etiquette`
  ADD PRIMARY KEY (`id_tache`,`id_etiquette`),
  ADD KEY `id_etiquette` (`id_etiquette`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `colonne`
--
ALTER TABLE `colonne`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT pour la table `etiquette`
--
ALTER TABLE `etiquette`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT pour la table `projet`
--
ALTER TABLE `projet`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT pour la table `tache`
--
ALTER TABLE `tache`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=222;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `colonne2tache`
--
ALTER TABLE `colonne2tache`
  ADD CONSTRAINT `colonne2tache_ibfk_1` FOREIGN KEY (`id_colonne`) REFERENCES `colonne` (`id`),
  ADD CONSTRAINT `colonne2tache_ibfk_2` FOREIGN KEY (`id_tache`) REFERENCES `tache` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `dependance`
--
ALTER TABLE `dependance`
  ADD CONSTRAINT `dependance_ibfk_1` FOREIGN KEY (`id_tache_mere`) REFERENCES `tache` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `dependance_ibfk_2` FOREIGN KEY (`id_sous_tache`) REFERENCES `tache` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `projet2colonne`
--
ALTER TABLE `projet2colonne`
  ADD CONSTRAINT `projet2colonne_ibfk_1` FOREIGN KEY (`id_projet`) REFERENCES `projet` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `projet2colonne_ibfk_2` FOREIGN KEY (`id_colonne`) REFERENCES `colonne` (`id`);

--
-- Contraintes pour la table `tache2etiquette`
--
ALTER TABLE `tache2etiquette`
  ADD CONSTRAINT `tache2etiquette_ibfk_1` FOREIGN KEY (`id_tache`) REFERENCES `tache` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tache2etiquette_ibfk_2` FOREIGN KEY (`id_etiquette`) REFERENCES `etiquette` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
