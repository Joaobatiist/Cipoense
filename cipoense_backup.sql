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
INSERT INTO `analise_ia` VALUES (_binary '�\�P\�X\�Dܟ[)��/	','joao@gmail.com.br','2025-10-22 15:09:27.072707','Você é um treinador de futebol inteligente e experiente. Sua tarefa é analisar o desempenho do atleta joao. A análise deve ser para que o atleta melhora e se mantenha, com um tom de voz claro e de treinador. A resposta deve ser organizada em seções claras, sem usar negrito em qualquer parte do texto. Use parágrafos ou marcadores simples para cada item. Use as seguintes informações para a sua análise: \n\nDados Históricos: joao\n--- Avaliação Mais Recente (--- Avaliação em 22/10/2025 ---\nDesempenho: Jogo Defensivo: 3, Manuseio de Bola: 3, Passe: 3, Recepção: 3, Jogo Ofensivo: 3, Controle de Bola: 3, Tiro: 3, Dribles: 3, Cruzamento: 3, Gerenciamento de Gols: 3, Giro: 3, Força no Chute: 3\nTático/Psicológico/Físico: Confiança: 3, Trabalho em Equipe: 3, Tomada de Decisões: 3, Disciplina: 3, Liderança: 3, Foco: 3, Atuar Sob Pressão: 3, Atributos Físicos: 3, Compromisso: 3, Esportividade: 3\nFeedback Qualitativo: \"b\n\nb\n\nb\n\nb\n\nb\n\nb\"\n\n) ---\nDesempenho: 22/10/2025\nTático/Psicológico/Físico: Jogo Defensivo: 3, Manuseio de Bola: 3, Passe: 3, Recepção: 3, Jogo Ofensivo: 3, Controle de Bola: 3, Tiro: 3, Dribles: 3, Cruzamento: 3, Gerenciamento de Gols: 3, Giro: 3, Força no Chute: 3\nFeedback Qualitativo Recente: \"Confiança: 3, Trabalho em Equipe: 3, Tomada de Decisões: 3, Disciplina: 3, Liderança: 3, Foco: 3, Atuar Sob Pressão: 3, Atributos Físicos: 3, Compromisso: 3, Esportividade: 3\"\n\nCom base nesses dados, forneça uma análise detalhada, focando nos seguintes pontos:\n\n1. Pontos Fortes:\n- Destaque 3 a 5 pontos fortes claros do atleta, com exemplos ou tendências de melhoria ao longo do tempo.\n\n2. Áreas de Aprimoramento:\n- Identifique 3 a 5 áreas específicas onde o atleta pode melhorar. Seja direto e construtivo. Inclua  treino específicas para oque o pode melhorar atleta.\n\n3. Plano de Ação:\n- Para cada área de aprimoramento, sugira 1-2 ações práticas e concretas que o atleta pode realizar nos treinos e jogos para desenvolver essas habilidades.\n\n4. Previsão de Desempenho:\n- Ofereça uma previsão realista do que o atleta pode alcançar se esforçar mais, baseando-se nas tendências e no compromisso com o plano de ação.\n\n5. Mensagem Final:\n- Escreva uma mensagem de encorajamento, finalizando a análise.','Joao, vamos analisar seu desempenho para que você continue evoluindo.\n\n1. Pontos Fortes:\n\n*   Regularidade: Sua avaliação demonstra um nível consistente em todas as áreas, tanto técnicas quanto táticas e psicológicas. Isso indica uma base sólida para construir um desempenho ainda melhor.\n*   Equilíbrio: Você apresenta uma distribuição equilibrada de habilidades. Não há nenhuma área com deficiência gritante, o que facilita o desenvolvimento geral do seu jogo.\n*   Potencial: Com todas as suas habilidades avaliadas em 3, você tem um grande potencial de crescimento em todas as áreas. Isso significa que com o treinamento certo e dedicação, você pode melhorar significativamente seu desempenho geral.\n\n2. Áreas de Aprimoramento:\n\n*   Jogo Defensivo: Apesar de ter uma avaliação média, o jogo defensivo é crucial. Você precisa ser mais agressivo na marcação e antecipar as jogadas do adversário.\n*   Jogo Ofensivo: É importante desenvolver sua capacidade de criação de jogadas e finalização. Você precisa ser mais incisivo no ataque e buscar mais oportunidades de gol.\n*   Tomada de Decisões: Aprimorar sua capacidade de tomar decisões rápidas e eficazes em campo é fundamental. Você precisa analisar as situações de jogo com mais clareza e escolher a melhor opção.\n*   Atributos Físicos: Para alcançar um nível superior, é necessário investir no seu condicionamento físico. Melhore sua velocidade, força e resistência para ter um melhor desempenho durante toda a partida.\n\n3. Plano de Ação:\n\n*   Jogo Defensivo:\n    *   Treino específico de marcação individual e coletiva, focando na pressão sobre o portador da bola e no posicionamento tático.\n    *   Simulações de situações de jogo em que você precisa recuperar a bola rapidamente, como contra-ataques e transições defensivas.\n*   Jogo Ofensivo:\n    *   Treino de finalização com diferentes tipos de chute e ângulos, buscando precisão e potência.\n    *   Exercícios de criação de jogadas em espaços reduzidos, aprimorando sua capacidade de drible, passe e movimentação.\n*   Tomada de Decisões:\n    *   Análise de vídeos de jogos, identificando situações em que você poderia ter tomado decisões melhores e discutindo as opções com a comissão técnica.\n    *   Participação em jogos de treinamento com foco na tomada de decisões, recebendo feedback imediato sobre suas escolhas.\n*   Atributos Físicos:\n    *   Programa de treinamento físico individualizado, com foco em ganho de força, velocidade e resistência.\n    *   Acompanhamento nutricional para otimizar seu desempenho físico e recuperação.\n\n4. Previsão de Desempenho:\n\nCom um esforço contínuo e foco nas áreas de aprimoramento, você tem o potencial de se tornar um jogador muito mais completo e influente em campo. Se você seguir o plano de ação e se dedicar aos treinos, poderá aumentar suas avaliações em todas as áreas para um nível 4 ou 5 em um período de tempo razoável. Isso o tornará um jogador mais valioso para a equipe e abrirá novas oportunidades na sua carreira.\n\n5. Mensagem Final:\n\nJoao, você tem talento e potencial. Acredite em si mesmo, siga o plano de ação e trabalhe duro. Com dedicação e disciplina, você pode alcançar seus objetivos e se tornar um jogador de destaque. Estamos aqui para te apoiar em cada passo do caminho. Vamos juntos!\n');
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
  `documento_pdf_bytes` longtext,
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
INSERT INTO `atleta` VALUES (_binary '\�@H\"IŖ\�0+\�\�','179.873.817-98','2003-07-25',NULL,NULL,'joao@gmail.com.br','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','image/jpeg',_binary '\0',_binary '',80,704969,'joao','GOLEIRO','ATLETA','$2a$10$XT7tiGQzLoJ6MrR8aoCyTeoaF9DxHVwEhPOCxRk9YgJMdfDIexFSO','SUB_11',_binary ';\���!�Cg��9�\�}');
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
INSERT INTO `presenca` VALUES (_binary ':�ӆ=2F��Ֆ�e4�\�','2025-10-21',_binary '\0',_binary '\�@H\"IŖ\�0+\�\�');
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
INSERT INTO `relatorio_avaliacao_geral` VALUES (_binary '9\���\�B\r�ܤ\�\�\�I','b','2025-10-22','b','b','b','','b','b','GOLEIRO','SUB_11','joao',_binary '\�@H\"IŖ\�0+\�\�');
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
INSERT INTO `relatorio_tatico` VALUES (_binary 'Q\�z�QO���I��ٺ',3,3,3,3,3,3,3,3,3,3,_binary '\�@H\"IŖ\�0+\�\�',_binary '9\���\�B\r�ܤ\�\�\�I');
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
INSERT INTO `relatorio_tecnico` VALUES (_binary '�\\\�M/���I�',3,3,3,3,3,3,3,3,3,3,3,3,_binary '\�@H\"IŖ\�0+\�\�',_binary '9\���\�B\r�ܤ\�\�\�I');
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
INSERT INTO `reponsavel` VALUES (_binary ';\���!�Cg��9�\�}','129.289.179-13','ronald@gmail.com','joaoanand','(90) 18031-0313');
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
INSERT INTO `supervisor` VALUES (_binary '1\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','088.912.255-50','2003-07-25','joao@outlook.com','joao','SUPERVISOR','$2a$10$Xw8ckZTm3gfAUWE2qqTjL.iSVqSxWRGcY9QwETiq2.6V4GUNfUsgW','(71) 99922-2524');
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

-- Dump completed on 2025-10-22 14:52:08
