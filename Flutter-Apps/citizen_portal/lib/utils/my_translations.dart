import 'package:get/get.dart';

class StrRes extends Translations {
  @override
  Map<String, Map<String, String>> get keys {
    return {'en': enMap(), 'pt': ptMap()};
  }

  static const login = "login";
  static const register = "register";
  static const contact = "contact";
  static const newUser = "new_user";
  static const email = "email";
  static const website = "website";
  static const home = "home";
  static const faq = "faq";
  static const qlinks = "qlinks";
  static const link1 = "link1";
  static const link2 = "link2";
  static const link3 = "link3";
  static const link4 = "link4";
  static const link5 = "link5";
  static const lupdates = "lupdates";
  static const visits = "visits";
  static const copyRight = "copyRight";
  static const faqHelpMsg = "faqHelpMsg";
  static const search = "search";
  static const videos = "videos";
  static const topics = "topics";

  Map<String, String> enMap() {
    return {
      contact: 'Contact Us',
      register: 'Register',
      login: 'Login',
      newUser: 'New User?',
      email: 'Email',
      website: 'WebSite',
      home: 'Home',
      faq: 'FAQ',
      qlinks: 'Quick links',
      link1: 'Tax Payments',
      link2: 'Check Service Request Status',
      link3: 'Check Vehicle Status',
      link4: 'Check Violations',
      link5: 'NUIT Verification',
      lupdates: 'Latest Updates',
      visits: 'Visitor\'s Count: %s',
      copyRight: 'All rights reserved Powered By SGS Tax360',
      faqHelpMsg: 'How can we help you?',
      search: 'Search',
      videos: 'Videos',
      topics: 'Topics',
    };
  }

  Map<String, String> ptMap() {
    return {
      contact: 'Contactos',
      register: 'Registo',
      login: 'Iniciar a Sessão',
      newUser: 'Registar Novo Munícipe',
      email: 'Email',
      website: 'Página web',
      home: 'Página inicial',
      faq: 'Perguntas Frequentes',
      qlinks: 'Links Rápidos',
      link1: 'Pagamento de tributo',
      link2: 'Verificar o estado da solicitação de serviço',
      link3: 'Verificar o estado do veículo',
      link4: 'Verificar infracções',
      link5: 'Verificação de NUIT',
      lupdates: 'Actualizações mais recentes',
      visits: 'Número de visitantes: %s',
      copyRight: 'Todos direitos reservados. Desenvolvido pela SGS Tax360',
      faqHelpMsg: 'Precisa de ajuda?',
      search: 'Pesquisar',
      videos: 'Vídeos',
      topics: 'Tópicos',
    };
  }
}
