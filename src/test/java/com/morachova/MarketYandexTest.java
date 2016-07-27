package com.morachova;

import com.codeborne.selenide.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertEquals;


public class MarketYandexTest {

    @Before
    //Before и After в дальнейшем можно вынести в отдельный класс для упрощения работы с тестами, в данном случае оставила как есть в виду всего одной задачи
    public void openYandexMarket() {
        open("http://market.yandex.ru/");
    }

    @After
    public void postClearData() {
        executeJavaScript("localStorage.clear()"); //Тоже не обязательная часть, но гарантирует нам дальнейший прогон тестов на чистом окружении.
        open("http://google.com");
    }

    @Test
    public void testNokiaLumia930Search() {
        search("nokia"); //унифицировала строку поиска на главной странице для универсальности использования метода в дальнейших тестах
        chooseCategory("Мобильные телефоны"); //как и в предыдущем варианте, при помощи Селенида мы можем этот метод использовать для перехода в любую категорию без необходимости подбора сложных селекторов

        for (int x = 0; x <= 10; x++) { //здесь можно было бы сразу прочесть какая у нас максимальная страница, вынести в int и использовать как макс. Я решила, что тест проверки более чем 10 страниц в любом случае будет очень долго прогонятся. Но при необходимости можно сделать правильнее
            if (searchItem.is(visible)) { //searchItem - переменная которая повторяется в коде дважды, в случае если мы будем искать другой предмет - проще заменить в одном месте, чем искать по всему коду. В случае множества предметов, конечно, такой подход надо будет переделать. Решила здесь не усложнять, а просто выполнить условия задания.
                openFoundResult(); //проверка которой не было в задании, но так мы точно не только нашли, но и можем открыть элемент и удостоверится, что страница прогрузилась.
                break;
            } else if (nextPage.is(visible)) { //продолжаем проверять по-странично пока не найдем искомое
                System.out.println("Phone is not found in current page results"); //не обязательные стринги, можно их удалить
                nextPage.click();
            } else { //в случае если страницы либо закончились, либо мы дошли до 10-й, поиск остановится сообщив нам, что ничего не найдено
                System.out.println("Phone is not found in all search results pages");
                break;
            }
        }
    }

    SelenideElement nextPage = $(".button_side_right.button_type_arrow");
    SelenideElement searchItem = $(By.linkText("Nokia Lumia 930")); //также можно искать через css ".snippet-card__header-text" и по содержанию всех найденных элементов, но по точному тексту проще читается и/или меняется на любое другое значение

    public void search(String searchText) {
        $("#header-search").setValue(searchText).pressEnter();
    }

    public void chooseCategory(String category) {
        $(By.linkText(category)).click(); //Или, также, CSS selector ".top5categ__img:first-child>a", но тогда пришлось бы каждый раз подбирать новый селектор или не выносить в удобный читаемый метод
       /* $$(".snippet-card__header-text").shouldHaveSize(10); //это неявная проверка. Я осознаю, что если у нас результатов менее 10, то тест завалится. Но в случаях когда мы на 100% уверены (к примеру в аналогичном поиске в гугле), что результатов больше - она позволяет убедится, что наш поиск отработал корректно - выдал результаты. Оставляю здесь в виде комментария просто как пример */
    }

    public void openFoundResult() {
        searchItem.click();
        assertEquals("Купить смартфон Nokia Lumia 930 — выгодные цены на Яндекс.Маркете", title()); //неявная проверка того, что страница открылась и подгрузилась.
    }
}
