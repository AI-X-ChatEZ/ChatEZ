import requests
from bs4 import BeautifulSoup


class BlogMarket:
    def __init__(self):
        pass

    def extract_contents(self, data):
        text = ""
        file_name = ""

        for i in range(len(data)):
            text += data[i].text
            text += "\n"

            if i == 0:
                file_name = data[i].text

        return text, file_name


class Nshopping_Category:
    def __init__(self):
        pass

    def get_name(self, text):
        name_start = '"exceptionalCategoryTypes":["REGULAR_SUBSCRIPTION","FREE_RETURN_INSURANCE"]},"name":"'
        name_end = '","productUrl":'

        start_idx = text.index(name_start) + len(name_start)
        temp = text[start_idx:]
        end_idx = temp.index(name_end)
        product_name = temp[:end_idx]

        return product_name

    def get_detail(self, text):
        start_word = '"detailContentText"'
        end_word = '"excessDetailContentText"'

        start_idx = text.index(start_word) + len(start_word) + len(':"')
        end_idx = text.index(end_word) - len('",')
        product_detail = text[start_idx:end_idx]

        return product_detail


if __name__ == '__main__':
    market_type = input("Type the market type. 'blog' or 'NshopCate'? ")

    urls = []
    url = ""
    while url != 'done':
        url = input("Put a url here:")
        urls.append(url)

    if market_type == 'blog':
        for i, url in enumerate(urls):
            if i == len(urls) - 1:
                break

            response = requests.get(url)
            content = BeautifulSoup(response.content, "html.parser")

            blog_scraper = BlogMarket()

            text, file_name = blog_scraper.extract_contents(content.find_all('p'))
            with open("./" + file_name + ".txt", 'w+', encoding='utf-8') as f:
                f.write(text)

    elif market_type == 'NshopCate':
        for i, url in enumerate(urls):
            if i == len(urls) - 1:
                break

            try:
                response = requests.get(url)
                content = BeautifulSoup(response.content, "html.parser")

                text = content.find_all('script')[1].text
                NshopCate_scraper = Nshopping_Category()

                name = NshopCate_scraper.get_name(text)
                detail = NshopCate_scraper.get_detail(text)

                with open("./" + name + ".txt", 'w+', encoding='utf-8') as f:
                    f.write(detail)
            except:
                print(url)