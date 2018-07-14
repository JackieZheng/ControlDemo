package com.myapplication.api;

import android.accounts.NetworkErrorException;
import com.example.mylibrary.base.ApiResponse;
import com.myapplication.R;
import com.myapplication.bean.Result;
import com.myapplication.bean.User;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

/**
 * 模拟API
 */
public abstract class MockApi {
  private static Random mRandom = new Random();
  private static int id =4; //mRandom.nextInt(8);
  private static int m = 0;

  @GET("/account/accountInfo")
  abstract Observable<ApiResponse<List<User>>> getUser(@Query("page") int page,
      @Query("pagesize") int pageSize);

  /**
   * 模拟获取数据,延时1500毫秒返回数据
   *
   * @param count 获取多少条数据
   * @return {@link Observable}
   */
  public static Observable<ApiResponse<List<Result>>> queryData(final int count) {
    return Observable.timer(1500, TimeUnit.MILLISECONDS)
        .flatMap(new Function<Long, ObservableSource<ApiResponse<List<Result>>>>() {
          @Override public ObservableSource<ApiResponse<List<Result>>> apply(Long aLong)
              throws Exception {
            return Observable.create(new ObservableOnSubscribe<ApiResponse<List<Result>>>() {
              @Override public void subscribe(ObservableEmitter<ApiResponse<List<Result>>> e)
                  throws Exception {
                int index = id % 8;
                ApiResponse<List<Result>> data = new ApiResponse<List<Result>>();
                Timber.e("" + index);
                switch (index) {
                  case 0:
                  case 2:
                  case 4:
                  case 6: {//有数据
                    data.setCode(0);
                    data.setData(getRandomSublist(count));
                    e.onNext(data);
                    e.onComplete();
                    break;
                  }
                  case 1: {//网络异常,链接超时等
                    e.onError(new NetworkErrorException("网络异常"));
                    break;
                  }
                  case 3: {//本地一般错误,如JSON转换异常,代码错误等
                    e.onError(new RuntimeException("模拟异常情况"));
                    break;
                  }
                  case 5: {//服务端异常
                    data.setCode(1);
                    data.setMessage("服务端异常");
                    e.onNext(data);
                    e.onComplete();
                    break;
                  }
                  case 7: {//服务端已经没有更多数据
                    data.setCode(0);
                    e.onNext(data);
                    e.onComplete();
                    break;
                  }
                }
                if (index == 0) {
                  if (++m % 3 == 0) {//连续三次加载有数据
                    id++;
                  }
                } else {
                  id++;
                }
              }
            });
          }
        });
  }

  public static void test() {
    Api.create(MockApi.class)//
        .getUser(1, 20)//
        .doOnNext(new Consumer<ApiResponse<List<User>>>() {
          @Override public void accept(@NonNull ApiResponse<List<User>> response) throws Exception {
          }
        })//
        .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
        .doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(@NonNull Disposable disposable) throws Exception {
          }
        })//开始执行之前的准备工作
        .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
        .observeOn(AndroidSchedulers.mainThread())//指定这行代码之后的subscribe在io线程执行
        .subscribe(new Consumer<ApiResponse<List<User>>>() {
          @Override public void accept(@NonNull ApiResponse<List<User>> response) throws Exception {
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(@NonNull Throwable throwable) throws Exception {
            Timber.e(throwable);
          }
        });
  }

  /**
   * 获取指定书目的数据
   *
   * @param amount 数据数量
   * @return {@link List}
   */
  private static List<Result> getRandomSublist(int amount) {
    ArrayList<Result> list = new ArrayList<>(amount);
    Random random = new Random();
    while (list.size() < amount) {
      Result result = new Result();
      result.setName(sCheeseStrings[random.nextInt(sCheeseStrings.length)]);
      result.setIcon(getRandomCheeseDrawable(random.nextInt(sCheeseStrings.length)));
      list.add(result);
    }
    return list;
  }

  private static int getRandomCheeseDrawable(int index) {
    index = index % 5;
    switch (index) {
      default:
      case 0:
        return R.drawable.cheese_1;
      case 1:
        return R.drawable.cheese_2;
      case 2:
        return R.drawable.cheese_3;
      case 3:
        return R.drawable.cheese_4;
      case 4:
        return R.drawable.cheese_5;
    }
  }

  private static final String[] sCheeseStrings = {
      "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi", "Acorn",
      "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
      "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese", "Ami du Chambertin",
      "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell", "Aragon", "Ardi Gasna",
      "Ardrahan", "Armenian String", "Aromes au Gene de Marc", "Asadero", "Asiago",
      "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss", "Babybel", "Baguette Laonnaise",
      "Bakers", "Baladi", "Balaton", "Bandal", "Banon", "Barry's Bay Cheddar", "Basing",
      "Basket Cheese", "Bath Cheese", "Bavarian Bergkase", "Baylough", "Beaufort", "Beauvoorde",
      "Beenleigh Blue", "Beer Cheese", "Bel Paese", "Bergader", "Bergere Bleue", "Berkswell",
      "Beyaz Peynir", "Bierkase", "Bishop Kennedy", "Blarney", "Bleu d'Auvergne", "Bleu de Gex",
      "Bleu de Laqueuille", "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello",
      "Blue Rathgore", "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini",
      "Bocconcini (Australian)", "Boeren Leidenkaas", "Bonchester", "Bosworth", "Bougon",
      "Boule Du Roves", "Boulette d'Avesnes", "Boursault", "Boursin", "Bouyssou", "Bra",
      "Braudostur", "Breakfast Cheese", "Brebis du Lavort", "Brebis du Lochois",
      "Brebis du Puyfaucon", "Bresse Bleu", "Brick", "Brie", "Brie de Meaux", "Brie de Melun",
      "Brillat-Savarin", "Brin", "Brin d' Amour", "Brin d'Amour", "Brinza (Burduf Brinza)",
      "Briquette de Brebis", "Briquette du Forez", "Broccio", "Broccio Demi-Affine",
      "Brousse du Rove", "Bruder Basil", "Brusselae Kaas (Fromage de Bruxelles)", "Bryndza",
      "Buchette d'Anjou", "Buffalo", "Burgos", "Butte", "Butterkase", "Button (Innes)",
      "Buxton Blue", "Cabecou", "Caboc", "Cabrales", "Cachaille", "Caciocavallo", "Caciotta",
      "Caerphilly", "Cairnsmore", "Calenzana", "Cambazola", "Camembert de Normandie",
      "Canadian Cheddar", "Canestrato", "Cantal", "Caprice des Dieux", "Capricorn Goat",
      "Capriole Banon", "Carre de l'Est", "Casciotta di Urbino", "Cashel Blue", "Castellano",
      "Castelleno", "Castelmagno", "Castelo Branco", "Castigliano", "Cathelain", "Celtic Promise",
      "Cendre d'Olivet", "Cerney", "Chabichou", "Chabichou du Poitou", "Chabis de Gatine",
      "Chaource", "Charolais", "Chaumes", "Cheddar", "Cheddar Clothbound", "Cheshire", "Chevres",
      "Chevrotin des Aravis", "Chontaleno", "Civray", "Coeur de Camembert au Calvados",
      "Coeur de Chevre", "Colby", "Cold Pack", "Comte", "Coolea", "Cooleney", "Coquetdale",
      "Corleggy", "Cornish Pepper", "Cotherstone", "Cotija", "Cottage Cheese",
      "Cottage Cheese (Australian)", "Cougar Gold", "Coulommiers", "Coverdale", "Crayeux de Roncq",
      "Cream Cheese", "Cream Havarti", "Crema Agria", "Crema Mexicana", "Creme Fraiche",
      "Crescenza", "Croghan", "Crottin de Chavignol", "Crottin du Chavignol", "Crowdie", "Crowley",
      "Cuajada", "Curd", "Cure Nantais", "Curworthy", "Cwmtawe Pecorino", "Cypress Grove Chevre",
      "Danablu (Danish Blue)", "Danbo", "Danish Fontina", "Daralagjazsky", "Dauphin",
      "Delice des Fiouves", "Denhany Dorset Drum", "Derby", "Dessertnyj Belyj", "Devon Blue",
      "Devon Garland", "Dolcelatte", "Doolin", "Doppelrhamstufel", "Dorset Blue Vinney",
      "Double Gloucester", "Double Worcester", "Dreux a la Feuille", "Dry Jack", "Duddleswell",
      "Dunbarra", "Dunlop", "Dunsyre Blue", "Duroblando", "Durrus",
      "Dutch Mimolette (Commissiekaas)", "Edam", "Edelpilz", "Emental Grand Cru", "Emlett",
      "Emmental", "Epoisses de Bourgogne", "Esbareich", "Esrom", "Etorki",
      "Evansdale Farmhouse Brie", "Evora De L'Alentejo", "Exmoor Blue", "Explorateur", "Feta",
      "Feta (Australian)", "Figue", "Filetta", "Fin-de-Siecle", "Finlandia Swiss", "Finn",
      "Fiore Sardo", "Fleur du Maquis", "Flor de Guia", "Flower Marie", "Folded",
      "Folded cheese with mint", "Fondant de Brebis", "Fontainebleau", "Fontal",
      "Fontina Val d'Aosta", "Formaggio di capra", "Fougerus", "Four Herb Gouda",
      "Fourme d' Ambert", "Fourme de Haute Loire", "Fourme de Montbrison", "Fresh Jack",
      "Fresh Mozzarella", "Fresh Ricotta", "Fresh Truffles", "Fribourgeois", "Friesekaas",
      "Friesian", "Friesla", "Frinault", "Fromage a Raclette", "Fromage Corse",
      "Fromage de Montagne de Savoie", "Fromage Frais", "Fruit Cream Cheese", "Frying Cheese",
      "Fynbo", "Gabriel", "Galette du Paludier", "Galette Lyonnaise", "Galloway Goat's Milk Gems",
      "Gammelost", "Gaperon a l'Ail", "Garrotxa", "Gastanberra", "Geitost", "Gippsland Blue",
      "Gjetost", "Gloucester", "Golden Cross", "Gorgonzola", "Gornyaltajski", "Gospel Green",
      "Gouda", "Goutu", "Gowrie", "Grabetto", "Graddost", "Grafton Village Cheddar", "Grana",
      "Grana Padano", "Grand Vatel", "Grataron d' Areches", "Gratte-Paille", "Graviera", "Greuilh",
      "Greve", "Gris de Lille", "Gruyere", "Gubbeen", "Guerbigny", "Halloumi",
      "Halloumy (Australian)", "Haloumi-Style Cheese", "Harbourne Blue", "Havarti", "Heidi Gruyere",
      "Hereford Hop", "Herrgardsost", "Herriot Farmhouse", "Herve", "Hipi Iti",
      "Hubbardston Blue Cow", "Hushallsost", "Iberico", "Idaho Goatster", "Idiazabal",
      "Il Boschetto al Tartufo", "Ile d'Yeu", "Isle of Mull", "Jarlsberg", "Jermi Tortes",
      "Jibneh Arabieh", "Jindi Brie", "Jubilee Blue", "Juustoleipa", "Kadchgall", "Kaseri",
      "Kashta", "Kefalotyri", "Kenafa", "Kernhem", "Kervella Affine", "Kikorangi",
      "King Island Cape Wickham Brie", "King River Gold", "Klosterkaese", "Knockalara", "Kugelkase",
      "L'Aveyronnais", "L'Ecir de l'Aubrac", "La Taupiniere", "La Vache Qui Rit", "Laguiole",
      "Lairobell", "Lajta", "Lanark Blue", "Lancashire", "Langres", "Lappi", "Laruns", "Lavistown",
      "Le Brin", "Le Fium Orbo", "Le Lacandou", "Le Roule", "Leafield", "Lebbene", "Leerdammer",
      "Leicester", "Leyden", "Limburger", "Lincolnshire Poacher", "Lingot Saint Bousquet d'Orb",
      "Liptauer", "Little Rydings", "Livarot", "Llanboidy", "Llanglofan Farmhouse",
      "Loch Arthur Farmhouse", "Loddiswell Avondale", "Longhorn", "Lou Palou", "Lou Pevre",
      "Lyonnais", "Maasdam", "Macconais", "Mahoe Aged Gouda", "Mahon", "Malvern", "Mamirolle",
      "Manchego", "Manouri", "Manur", "Marble Cheddar", "Marbled Cheeses", "Maredsous", "Margotin",
      "Maribo", "Maroilles", "Mascares", "Mascarpone", "Mascarpone (Australian)",
      "Mascarpone Torta", "Matocq", "Maytag Blue", "Meira", "Menallack Farmhouse", "Menonita",
      "Meredith Blue", "Mesost", "Metton (Cancoillotte)", "Meyer Vintage Gouda", "Mihalic Peynir",
      "Milleens", "Mimolette", "Mine-Gabhar", "Mini Baby Bells", "Mixte", "Molbo",
      "Monastery Cheeses", "Mondseer", "Mont D'or Lyonnais", "Montasio", "Monterey Jack",
      "Monterey Jack Dry", "Morbier", "Morbier Cru de Montagne", "Mothais a la Feuille",
      "Mozzarella", "Mozzarella (Australian)", "Mozzarella di Bufala", "Mozzarella Fresh, in water",
      "Mozzarella Rolls", "Munster", "Murol", "Mycella", "Myzithra", "Naboulsi", "Nantais",
      "Neufchatel", "Neufchatel (Australian)", "Niolo", "Nokkelost", "Northumberland", "Oaxaca",
      "Olde York", "Olivet au Foin", "Olivet Bleu", "Olivet Cendre", "Orkney Extra Mature Cheddar",
      "Orla", "Oschtjepka", "Ossau Fermier", "Ossau-Iraty", "Oszczypek", "Oxford Blue",
      "P'tit Berrichon", "Palet de Babligny", "Paneer", "Panela", "Pannerone", "Pant ys Gawn",
      "Parmesan (Parmigiano)", "Parmigiano Reggiano", "Pas de l'Escalette", "Passendale",
      "Pasteurized Processed", "Pate de Fromage", "Patefine Fort", "Pave d'Affinois", "Pave d'Auge",
      "Pave de Chirac", "Pave du Berry", "Pecorino", "Pecorino in Walnut Leaves", "Pecorino Romano",
      "Peekskill Pyramid", "Pelardon des Cevennes", "Pelardon des Corbieres", "Penamellera",
      "Penbryn", "Pencarreg", "Perail de Brebis", "Petit Morin", "Petit Pardou", "Petit-Suisse",
      "Picodon de Chevre", "Picos de Europa", "Piora", "Pithtviers au Foin", "Plateau de Herve",
      "Plymouth Cheese", "Podhalanski", "Poivre d'Ane", "Polkolbin", "Pont l'Eveque",
      "Port Nicholson", "Port-Salut", "Postel", "Pouligny-Saint-Pierre", "Pourly", "Prastost",
      "Pressato", "Prince-Jean", "Processed Cheddar", "Provolone", "Provolone (Australian)",
      "Pyengana Cheddar", "Pyramide", "Quark", "Quark (Australian)", "Quartirolo Lombardo",
      "Quatre-Vents", "Quercy Petit", "Queso Blanco", "Queso Blanco con Frutas --Pina y Mango",
      "Queso de Murcia", "Queso del Montsec", "Queso del Tietar", "Queso Fresco",
      "Queso Fresco (Adobera)", "Queso Iberico", "Queso Jalapeno", "Queso Majorero",
      "Queso Media Luna", "Queso Para Frier", "Queso Quesadilla", "Rabacal", "Raclette", "Ragusano",
      "Raschera", "Reblochon", "Red Leicester", "Regal de la Dombes", "Reggianito", "Remedou",
      "Requeson", "Richelieu", "Ricotta", "Ricotta (Australian)", "Ricotta Salata", "Ridder",
      "Rigotte", "Rocamadour", "Rollot", "Romano", "Romans Part Dieu", "Roncal", "Roquefort",
      "Roule", "Rouleau De Beaulieu", "Royalp Tilsit", "Rubens", "Rustinu", "Saaland Pfarr",
      "Saanenkaese", "Saga", "Sage Derby", "Sainte Maure", "Saint-Marcellin", "Saint-Nectaire",
      "Saint-Paulin", "Salers", "Samso", "San Simon", "Sancerre", "Sap Sago", "Sardo",
      "Sardo Egyptian", "Sbrinz", "Scamorza", "Schabzieger", "Schloss", "Selles sur Cher", "Selva",
      "Serat", "Seriously Strong Cheddar", "Serra da Estrela", "Sharpam", "Shelburne Cheddar",
      "Shropshire Blue", "Siraz", "Sirene", "Smoked Gouda", "Somerset Brie", "Sonoma Jack",
      "Sottocenare al Tartufo", "Soumaintrain", "Sourire Lozerien", "Spenwood",
      "Sraffordshire Organic", "St. Agur Blue Cheese", "Stilton", "Stinking Bishop", "String",
      "Sussex Slipcote", "Sveciaost", "Swaledale", "Sweet Style Swiss", "Swiss",
      "Syrian (Armenian String)", "Tala", "Taleggio", "Tamie", "Tasmania Highland Chevre Log",
      "Taupiniere", "Teifi", "Telemea", "Testouri", "Tete de Moine", "Tetilla", "Texas Goat Cheese",
      "Tibet", "Tillamook Cheddar", "Tilsit", "Timboon Brie", "Toma", "Tomme Brulee",
      "Tomme d'Abondance", "Tomme de Chevre", "Tomme de Romans", "Tomme de Savoie",
      "Tomme des Chouans", "Tommes", "Torta del Casar", "Toscanello", "Touree de L'Aubier",
      "Tourmalet", "Trappe (Veritable)", "Trois Cornes De Vendee", "Tronchon", "Trou du Cru",
      "Truffe", "Tupi", "Turunmaa", "Tymsboro", "Tyn Grug", "Tyning", "Ubriaco", "Ulloa",
      "Vacherin-Fribourgeois", "Valencay", "Vasterbottenost", "Venaco", "Vendomois", "Vieux Corse",
      "Vignotte", "Vulscombe", "Waimata Farmhouse Blue", "Washed Rind Cheese (Australian)",
      "Waterloo", "Weichkaese", "Wellington", "Wensleydale", "White Stilton",
      "Whitestone Farmhouse", "Wigmore", "Woodside Cabecou", "Xanadu", "Xynotyro", "Yarg Cornish",
      "Yarra Valley Pyramid", "Yorkshire Blue", "Zamorano", "Zanetti Grana Padano",
      "Zanetti Parmigiano Reggiano"
  };
}
