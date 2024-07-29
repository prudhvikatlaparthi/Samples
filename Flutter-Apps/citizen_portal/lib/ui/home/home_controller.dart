import 'package:citizen_portal/utils/my_translations.dart';
import 'package:get/get.dart';

class HomeController extends GetxController {
  final img1 = "assets/images/img1.png";
  final img2 = "assets/images/img2.png";
  final openMenu = false.obs;

  final selectedMenu = Menu.home.obs;

  final quickMenuItems = [
    QuickMenuItem(StrRes.link1, QuickMenu.quickMenuTaxPayment),
    QuickMenuItem(StrRes.link2, QuickMenu.quickMenuSRStatus),
    QuickMenuItem(StrRes.link3, QuickMenu.quickMenuVEHStatus),
    QuickMenuItem(StrRes.link4, QuickMenu.quickMenuViolations),
    QuickMenuItem(StrRes.link5, QuickMenu.quickMenuNUITVerification),
  ];

  final contentHeader = "Casa de Ferro – Avenida Samora Machel";
  final content =
      " A construção de ferro situada a poucos metros da praça da Independência é um imóvel pré-fabricado, desenhado por ninguém menos do que Gustave Eiffel, em 1892. No entanto, a estrutura vinda da Bélgica acabou não cumprindo a finalidade de abrigar o então governador-geral de Moçambique, isso por conta do clima sub-tropical de Moçambique, que transformava o prédio em um verdadeiro “forno”. Atualmente, a Casa de Ferro abriga o Ministério da Cultura e do Turismo e pode ser visitada por dentro sem custo algum. No local — situado ao lado Jardim Botânico de Maputo —\no visitante encontra uma miniexposição de objetos de cidades medievais moçambicanas (no ar condicionado, é claro!).\nThank you!!";

  final videoUrls = [
    "uoIOQn8rPQw",
    "mn7fnqLrPQw",
    "Qhi6h10P4DQ",
    "WWns98034NU"
  ];

  final pdfUrls = [
    "https://citytaxobjectstore.sycotax.bf/Store/qamaputo/1.pdf",
    "https://citytaxobjectstore.sycotax.bf/Store/qamaputo/2.pdf",
    "https://citytaxobjectstore.sycotax.bf/Store/qamaputo/3.pdf",
    "https://citytaxobjectstore.sycotax.bf/Store/qamaputo/4.pdf",
  ];
}

class QuickMenuItem {
  QuickMenuItem(this.menu, this.code);
  String menu;
  QuickMenu code;
}

enum Menu { home, faq }

enum QuickMenu {
  quickMenuTaxPayment,
  quickMenuSRStatus,
  quickMenuVEHStatus,
  quickMenuViolations,
  quickMenuNUITVerification,
}
