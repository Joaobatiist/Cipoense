-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: localhost    Database: cipoense
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `analise_ia`
--

DROP TABLE IF EXISTS `analise_ia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `analise_ia` (
  `id` binary(16) NOT NULL,
  `atleta_email` varchar(255) NOT NULL,
  `data_analise` datetime(6) NOT NULL,
  `prompt` text NOT NULL,
  `respostaia` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analise_ia`
--

LOCK TABLES `analise_ia` WRITE;
/*!40000 ALTER TABLE `analise_ia` DISABLE KEYS */;
/*!40000 ALTER TABLE `analise_ia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `atleta`
--

DROP TABLE IF EXISTS `atleta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `atleta` (
  `id` binary(16) NOT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `data_de_nascimento` date DEFAULT NULL,
  `documento_pdf_bytes` text,
  `documento_pdf_content_type` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `foto` text,
  `foto_content_type` varchar(255) DEFAULT NULL,
  `is_apto_para_jogar` bit(1) NOT NULL,
  `isencao` bit(1) DEFAULT NULL,
  `massa` double NOT NULL,
  `matricula` int NOT NULL,
  `nome` varchar(255) NOT NULL,
  `posicao` enum('ALA_DEFENSIVA_DIREITA','ALA_DEFENSIVA_ESQUERDA','ATACANTE','GOLEIRO','LATERAL_DIREITO','LATERAL_ESQUERDO','MEIA_ATACANTE','MEIA_CENTRAL','PONTA_DIREITA','PONTA_ESQUERDA','SEGUNDO_ATACANTE','VOLANTE','ZAGUEIRO') DEFAULT NULL,
  `roles` enum('ATLETA','COORDENADOR','SUPERVISOR','TECNICO') DEFAULT NULL,
  `senha` varchar(255) NOT NULL,
  `sub_divisao` enum('SUB_10','SUB_11','SUB_12','SUB_13','SUB_14','SUB_15','SUB_16','SUB_17','SUB_18','SUB_4','SUB_5','SUB_6','SUB_7','SUB_8','SUB_9') DEFAULT NULL,
  `responsavel_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7kmhjax2l3c876v7ooh42thms` (`email`),
  UNIQUE KEY `UKmw6y8aadt093lvx5uupy28w1w` (`matricula`),
  UNIQUE KEY `UK1o8agxjk1d62nporewtfaonnu` (`cpf`),
  UNIQUE KEY `UKh1k5gjj45akkxu3i66jk3m1ai` (`responsavel_id`),
  CONSTRAINT `FKbsj1wqace0brjngb11o5pjp53` FOREIGN KEY (`responsavel_id`) REFERENCES `reponsavel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `atleta`
--

LOCK TABLES `atleta` WRITE;
/*!40000 ALTER TABLE `atleta` DISABLE KEYS */;
/*!40000 ALTER TABLE `atleta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicado`
--

DROP TABLE IF EXISTS `comunicado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicado` (
  `id` binary(16) NOT NULL,
  `assunto` varchar(255) DEFAULT NULL,
  `data` date DEFAULT NULL,
  `mensagem` varchar(255) DEFAULT NULL,
  `remetente_coordenador_id` binary(16) DEFAULT NULL,
  `remetente_supervisor_id` binary(16) DEFAULT NULL,
  `remetente_tecnico_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKthw2wbdvl0utov5akhde08kdn` (`remetente_coordenador_id`),
  KEY `FKofe85cp2d6y5cb5h4gh32nv9b` (`remetente_tecnico_id`),
  KEY `FKh82bv5k7py06hk7axjtgqh7gq` (`remetente_supervisor_id`),
  CONSTRAINT `FKh82bv5k7py06hk7axjtgqh7gq` FOREIGN KEY (`remetente_supervisor_id`) REFERENCES `supervisor` (`id`),
  CONSTRAINT `FKofe85cp2d6y5cb5h4gh32nv9b` FOREIGN KEY (`remetente_tecnico_id`) REFERENCES `tecnico` (`id`),
  CONSTRAINT `FKthw2wbdvl0utov5akhde08kdn` FOREIGN KEY (`remetente_coordenador_id`) REFERENCES `coordenador` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicado`
--

LOCK TABLES `comunicado` WRITE;
/*!40000 ALTER TABLE `comunicado` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicado_destinatario_atletas`
--

DROP TABLE IF EXISTS `comunicado_destinatario_atletas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicado_destinatario_atletas` (
  `comunicado_id` binary(16) NOT NULL,
  `atleta_id` binary(16) NOT NULL,
  PRIMARY KEY (`comunicado_id`,`atleta_id`),
  KEY `FKs81j4rd8n65y22knvvjssijsh` (`atleta_id`),
  CONSTRAINT `FKgrcye2kh4tm918cm47pu8eyfi` FOREIGN KEY (`comunicado_id`) REFERENCES `comunicado` (`id`),
  CONSTRAINT `FKs81j4rd8n65y22knvvjssijsh` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicado_destinatario_atletas`
--

LOCK TABLES `comunicado_destinatario_atletas` WRITE;
/*!40000 ALTER TABLE `comunicado_destinatario_atletas` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicado_destinatario_atletas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicado_destinatario_coordenador`
--

DROP TABLE IF EXISTS `comunicado_destinatario_coordenador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicado_destinatario_coordenador` (
  `comunicado_id` binary(16) NOT NULL,
  `coordenador_id` binary(16) NOT NULL,
  PRIMARY KEY (`comunicado_id`,`coordenador_id`),
  KEY `FKjb5gl4cxnt85ka26hsr8lc848` (`coordenador_id`),
  CONSTRAINT `FKjb5gl4cxnt85ka26hsr8lc848` FOREIGN KEY (`coordenador_id`) REFERENCES `coordenador` (`id`),
  CONSTRAINT `FKjwkm7qvciq0y8dfuhcgihyx9i` FOREIGN KEY (`comunicado_id`) REFERENCES `comunicado` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicado_destinatario_coordenador`
--

LOCK TABLES `comunicado_destinatario_coordenador` WRITE;
/*!40000 ALTER TABLE `comunicado_destinatario_coordenador` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicado_destinatario_coordenador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicado_destinatario_supervisor`
--

DROP TABLE IF EXISTS `comunicado_destinatario_supervisor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicado_destinatario_supervisor` (
  `comunicado_id` binary(16) NOT NULL,
  `supervisor_id` binary(16) NOT NULL,
  PRIMARY KEY (`comunicado_id`,`supervisor_id`),
  KEY `FK23gqbv5s1d6nq5djjk3r8us0f` (`supervisor_id`),
  CONSTRAINT `FK23gqbv5s1d6nq5djjk3r8us0f` FOREIGN KEY (`supervisor_id`) REFERENCES `supervisor` (`id`),
  CONSTRAINT `FKfhph97t8ydjrplhm1q4qfk3uo` FOREIGN KEY (`comunicado_id`) REFERENCES `comunicado` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicado_destinatario_supervisor`
--

LOCK TABLES `comunicado_destinatario_supervisor` WRITE;
/*!40000 ALTER TABLE `comunicado_destinatario_supervisor` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicado_destinatario_supervisor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicado_destinatario_tecnico`
--

DROP TABLE IF EXISTS `comunicado_destinatario_tecnico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicado_destinatario_tecnico` (
  `comunicado_id` binary(16) NOT NULL,
  `tecnico_id` binary(16) NOT NULL,
  PRIMARY KEY (`comunicado_id`,`tecnico_id`),
  KEY `FK9jn5ue11l3tqmqww62hc02b9b` (`tecnico_id`),
  CONSTRAINT `FK9jn5ue11l3tqmqww62hc02b9b` FOREIGN KEY (`tecnico_id`) REFERENCES `tecnico` (`id`),
  CONSTRAINT `FKnsefmwwjr6xe719lee7mrnttn` FOREIGN KEY (`comunicado_id`) REFERENCES `comunicado` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicado_destinatario_tecnico`
--

LOCK TABLES `comunicado_destinatario_tecnico` WRITE;
/*!40000 ALTER TABLE `comunicado_destinatario_tecnico` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicado_destinatario_tecnico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicado_status_por_usuario`
--

DROP TABLE IF EXISTS `comunicado_status_por_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicado_status_por_usuario` (
  `id` binary(16) NOT NULL,
  `ocultado` bit(1) NOT NULL,
  `atleta_id` binary(16) DEFAULT NULL,
  `comunicado_id` binary(16) NOT NULL,
  `coordenador_id` binary(16) DEFAULT NULL,
  `supervisor_id` binary(16) DEFAULT NULL,
  `tecnico_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlke4exb7o90w4w2qcbnup7c7j` (`atleta_id`),
  KEY `FK77kfhwtkxv3db5w9f8y10w51n` (`comunicado_id`),
  KEY `FKce97e4jscula6iwyqfhyb9373` (`coordenador_id`),
  KEY `FKeqdcvaajjk7rr0s85u8ycsw6o` (`tecnico_id`),
  KEY `FKpkv869ks3ghgxyycjs9apxsvb` (`supervisor_id`),
  CONSTRAINT `FK77kfhwtkxv3db5w9f8y10w51n` FOREIGN KEY (`comunicado_id`) REFERENCES `comunicado` (`id`),
  CONSTRAINT `FKce97e4jscula6iwyqfhyb9373` FOREIGN KEY (`coordenador_id`) REFERENCES `coordenador` (`id`),
  CONSTRAINT `FKeqdcvaajjk7rr0s85u8ycsw6o` FOREIGN KEY (`tecnico_id`) REFERENCES `tecnico` (`id`),
  CONSTRAINT `FKlke4exb7o90w4w2qcbnup7c7j` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`),
  CONSTRAINT `FKpkv869ks3ghgxyycjs9apxsvb` FOREIGN KEY (`supervisor_id`) REFERENCES `supervisor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicado_status_por_usuario`
--

LOCK TABLES `comunicado_status_por_usuario` WRITE;
/*!40000 ALTER TABLE `comunicado_status_por_usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicado_status_por_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coordenador`
--

DROP TABLE IF EXISTS `coordenador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coordenador` (
  `id` binary(16) NOT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `roles` enum('ATLETA','COORDENADOR','SUPERVISOR','TECNICO') DEFAULT NULL,
  `senha` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgms1a224uj5ogo40lykbiqhn0` (`cpf`),
  UNIQUE KEY `UKrksus5dobmi9ftbllos24u1yq` (`email`),
  UNIQUE KEY `UK2freypw1ilebgonwhna5yg5d5` (`telefone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coordenador`
--

LOCK TABLES `coordenador` WRITE;
/*!40000 ALTER TABLE `coordenador` DISABLE KEYS */;
/*!40000 ALTER TABLE `coordenador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documento_atleta`
--

DROP TABLE IF EXISTS `documento_atleta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documento_atleta` (
  `id` binary(16) NOT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `atleta_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdj6nxn3ha26ixg5d5g5kig0ka` (`atleta_id`),
  CONSTRAINT `FKdj6nxn3ha26ixg5d5g5kig0ka` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documento_atleta`
--

LOCK TABLES `documento_atleta` WRITE;
/*!40000 ALTER TABLE `documento_atleta` DISABLE KEYS */;
/*!40000 ALTER TABLE `documento_atleta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estoque`
--

DROP TABLE IF EXISTS `estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estoque` (
  `id` binary(16) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `quantidade` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estoque`
--

LOCK TABLES `estoque` WRITE;
/*!40000 ALTER TABLE `estoque` DISABLE KEYS */;
/*!40000 ALTER TABLE `estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eventos`
--

DROP TABLE IF EXISTS `eventos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eventos` (
  `id` binary(16) NOT NULL,
  `data` varchar(255) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `horario` varchar(255) DEFAULT NULL,
  `local` varchar(255) DEFAULT NULL,
  `professor` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eventos`
--

LOCK TABLES `eventos` WRITE;
/*!40000 ALTER TABLE `eventos` DISABLE KEYS */;
/*!40000 ALTER TABLE `eventos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `presenca`
--

DROP TABLE IF EXISTS `presenca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `presenca` (
  `id` binary(16) NOT NULL,
  `data` date DEFAULT NULL,
  `presente` bit(1) NOT NULL,
  `atleta_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK781i6stshuiwnfq6gf2gt0hye` (`atleta_id`),
  CONSTRAINT `FK781i6stshuiwnfq6gf2gt0hye` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presenca`
--

LOCK TABLES `presenca` WRITE;
/*!40000 ALTER TABLE `presenca` DISABLE KEYS */;
/*!40000 ALTER TABLE `presenca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relatorio_avaliacao_geral`
--

DROP TABLE IF EXISTS `relatorio_avaliacao_geral`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relatorio_avaliacao_geral` (
  `id` binary(16) NOT NULL,
  `areas_aprimoramento` text,
  `data_avaliacao` date DEFAULT NULL,
  `feedback_avaliador` text,
  `feedback_treinador` text,
  `metas_objetivos` text,
  `periodo_treino` varchar(255) DEFAULT NULL,
  `pontos_fortes` text,
  `pontos_fracos` text,
  `posicao` enum('ALA_DEFENSIVA_DIREITA','ALA_DEFENSIVA_ESQUERDA','ATACANTE','GOLEIRO','LATERAL_DIREITO','LATERAL_ESQUERDO','MEIA_ATACANTE','MEIA_CENTRAL','PONTA_DIREITA','PONTA_ESQUERDA','SEGUNDO_ATACANTE','VOLANTE','ZAGUEIRO') DEFAULT NULL,
  `sub_divisao` enum('SUB_10','SUB_11','SUB_12','SUB_13','SUB_14','SUB_15','SUB_16','SUB_17','SUB_18','SUB_4','SUB_5','SUB_6','SUB_7','SUB_8','SUB_9') DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `atleta_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK242h1fnugtuvs4dda2wtgtyx8` (`atleta_id`),
  CONSTRAINT `FK242h1fnugtuvs4dda2wtgtyx8` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relatorio_avaliacao_geral`
--

LOCK TABLES `relatorio_avaliacao_geral` WRITE;
/*!40000 ALTER TABLE `relatorio_avaliacao_geral` DISABLE KEYS */;
/*!40000 ALTER TABLE `relatorio_avaliacao_geral` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relatorio_tatico`
--

DROP TABLE IF EXISTS `relatorio_tatico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relatorio_tatico` (
  `id` binary(16) NOT NULL,
  `atributos_fisicos` int DEFAULT NULL,
  `atuar_sob_pressao` int DEFAULT NULL,
  `compromisso` int DEFAULT NULL,
  `confianca` int DEFAULT NULL,
  `disciplina` int DEFAULT NULL,
  `esportividade` int DEFAULT NULL,
  `foco` int DEFAULT NULL,
  `lideranca` int DEFAULT NULL,
  `tomada_de_decisoes` int DEFAULT NULL,
  `trabalho_em_equipe` int DEFAULT NULL,
  `atleta_id` binary(16) NOT NULL,
  `relatorio_avaliacao_geral_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKekwyu8o5fcge64otjlh1k9j2s` (`relatorio_avaliacao_geral_id`),
  KEY `FKk3vgrctfpcuihqjudhi873m6w` (`atleta_id`),
  CONSTRAINT `FKb33wk34ag3hgb8mlrh37w2sv5` FOREIGN KEY (`relatorio_avaliacao_geral_id`) REFERENCES `relatorio_avaliacao_geral` (`id`),
  CONSTRAINT `FKk3vgrctfpcuihqjudhi873m6w` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relatorio_tatico`
--

LOCK TABLES `relatorio_tatico` WRITE;
/*!40000 ALTER TABLE `relatorio_tatico` DISABLE KEYS */;
/*!40000 ALTER TABLE `relatorio_tatico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relatorio_tecnico`
--

DROP TABLE IF EXISTS `relatorio_tecnico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relatorio_tecnico` (
  `id` binary(16) NOT NULL,
  `controle` int DEFAULT NULL,
  `cruzamento` int DEFAULT NULL,
  `dribles` int DEFAULT NULL,
  `forca_chute` int DEFAULT NULL,
  `gerenciamento_de_gols` int DEFAULT NULL,
  `giro` int DEFAULT NULL,
  `jogo_defensivo` int DEFAULT NULL,
  `jogo_ofensivo` int DEFAULT NULL,
  `manuseio_de_bola` int DEFAULT NULL,
  `passe` int DEFAULT NULL,
  `recepcao` int DEFAULT NULL,
  `tiro` int DEFAULT NULL,
  `atleta_id` binary(16) NOT NULL,
  `relatorio_avaliacao_geral_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3ngtgu2cun917joe61fa6pk6v` (`relatorio_avaliacao_geral_id`),
  KEY `FKnhpk58ufu01iq1ntj85y9lw3m` (`atleta_id`),
  CONSTRAINT `FK7f2d9tod7j42n4cke6a85ffby` FOREIGN KEY (`relatorio_avaliacao_geral_id`) REFERENCES `relatorio_avaliacao_geral` (`id`),
  CONSTRAINT `FKnhpk58ufu01iq1ntj85y9lw3m` FOREIGN KEY (`atleta_id`) REFERENCES `atleta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relatorio_tecnico`
--

LOCK TABLES `relatorio_tecnico` WRITE;
/*!40000 ALTER TABLE `relatorio_tecnico` DISABLE KEYS */;
/*!40000 ALTER TABLE `relatorio_tecnico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reponsavel`
--

DROP TABLE IF EXISTS `reponsavel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reponsavel` (
  `id` binary(16) NOT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKedrkfsakaqdh7sg97qfr10fnp` (`cpf`),
  UNIQUE KEY `UKhl1ath8e3fspfg7f17lke2div` (`email`),
  UNIQUE KEY `UK6ugo71fllmw00u6360fubsb6x` (`telefone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reponsavel`
--

LOCK TABLES `reponsavel` WRITE;
/*!40000 ALTER TABLE `reponsavel` DISABLE KEYS */;
/*!40000 ALTER TABLE `reponsavel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supervisor`
--

DROP TABLE IF EXISTS `supervisor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supervisor` (
  `id` binary(16) NOT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `roles` enum('ATLETA','COORDENADOR','SUPERVISOR','TECNICO') DEFAULT NULL,
  `senha` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKl87moali6unqmyflgxcfxgbae` (`cpf`),
  UNIQUE KEY `UK4hiyr861ioc0pcim0bihog2qb` (`email`),
  UNIQUE KEY `UK251dtgaax9h023gi1j7vxenx1` (`telefone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supervisor`
--

LOCK TABLES `supervisor` WRITE;
/*!40000 ALTER TABLE `supervisor` DISABLE KEYS */;
INSERT INTO `supervisor` VALUES (_binary '	\ö¾½±=LJ½Bi\Èh™$3','089.122.605-54','2003-07-25','jao@gmail.com','jao','SUPERVISOR','$2a$10$bI7yoB/3SvF0QeDaq7BmHeCEC4yklNCsa1o9A4hdkBNuyBqVWUcoK','(84) 54045-4845'),(_binary '1\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','088.912.255-50','2003-07-25','joao@outlook.com','joao','SUPERVISOR','$2a$10$Xw8ckZTm3gfAUWE2qqTjL.iSVqSxWRGcY9QwETiq2.6V4GUNfUsgW','(71) 99922-2524');
/*!40000 ALTER TABLE `supervisor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tecnico`
--

DROP TABLE IF EXISTS `tecnico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tecnico` (
  `id` binary(16) NOT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `roles` enum('ATLETA','COORDENADOR','SUPERVISOR','TECNICO') DEFAULT NULL,
  `senha` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcecbp9mq0y2qh6r4j6m8lc36a` (`cpf`),
  UNIQUE KEY `UKabgjgppc7qtaf1euio3pl39h8` (`email`),
  UNIQUE KEY `UKt1s086hr6papcbcryaen6ndfb` (`telefone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tecnico`
--

LOCK TABLES `tecnico` WRITE;
/*!40000 ALTER TABLE `tecnico` DISABLE KEYS */;
/*!40000 ALTER TABLE `tecnico` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-14 19:39:01
