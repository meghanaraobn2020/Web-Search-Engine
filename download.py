import requests
import re


def retrieve_text(topic):
    # this is the config for to get the first introduction of a title
    data_flag = True
    image_flag = True
    try:
        text_config = {
            'action': 'query',
            'format': 'json',
            'titles': topic,
            'prop': 'extracts',
            'exintro': True,
            'explaintext': True,
        }
        text_response = requests.get('https://en.wikipedia.org/w/api.php', params=text_config).json()
        text_page = next(iter(text_response['query']['pages'].values()))

        #print(text_page)
        if text_page['extract'] == '':
            #data_flag = False
            raise Exception("No data available for download")

        print("Writing Document...")
        file1 = open("../Dataset/" + text_page['title'] + ".txt", "w", encoding="utf-8")  # write mode
        temp = text_page['extract']  # .strip(u\u200b)
        file1.write(temp)
        file1.close()
    except:
        print("No data available for download")
        data_flag = False

    try:
        if data_flag == False:
            raise Exception("")
        num_image_config = {
            'action': 'parse',
            'pageid': text_page['pageid'],
            'format': 'json',
            'prop': 'images'
        }
        num_image_response = requests.get('https://en.wikipedia.org/w/api.php', params=num_image_config).json()

        # now that we have the number of images in the page, we ask for the images that are in the page with the title
        image_config = {
            'action': 'query',
            'format': 'json',
            'titles': topic,
            'prop': 'images',
            'imlimit': len(num_image_response['parse']['images'])
        }
        image_response = requests.get('https://en.wikipedia.org/w/api.php', params=image_config).json()
        image_page = next(iter(image_response['query']['pages'].values()))

        # and we  write the image files one by one in the current directory
        # we also do not write the svg files, since as they are mostly the logos
        # modify the filename_pattern regex for to accept the proper files

        #print(image_page)
        filename_pattern = re.compile(".*\.(?:jpe?g|gif|png|JPE?G|GIF|PNG)")

        print("Writing Images...")
        for i in range(len(image_page['images'])):

            url_config = {
                'action': 'query',
                'format': 'json',
                'titles': image_page['images'][i]['title'],
                'prop': 'imageinfo',
                'iiprop': 'url'
            }
            url_response = requests.get('https://en.wikipedia.org/w/api.php', params=url_config).json()
            url_page = next(iter(url_response['query']['pages'].values()))
            print(url_page['imageinfo'][0]['url'])
            if (filename_pattern.search(url_page['imageinfo'][0]['url'])):

                print("writing image " + url_page['imageinfo'][0]['url'].rsplit("/", 1)[1])
                with open("../Dataset/" + url_page['imageinfo'][0]['url'].rsplit("/", 1)[1], 'wb') as handle:
                    response = requests.get(url_page['imageinfo'][0]['url'], stream=True)

                    if not response.ok:
                        print(response)

                    for block in response.iter_content(1024):
                        if not block:
                            break

                        handle.write(block)

    except:
        print("No image to download")
        image_flag = False

    if image_flag == False or data_flag == False: return False

    return True


def FNameparser(path, start, end):
    Namelist = []
    Origlist = []
    with open(path, 'rb') as f:
        lines = [x.decode('utf8').strip() for x in f.readlines()]
        for i in range(start, end):
            l = list(lines[i])
            l = [e for e in l if e not in ('!', ',', '(', ')', '"')]
            op_str = ''.join(l)
            op_str = re.sub(r"(?i)^[^a-z\d()]*|[^a-z\d()]+$", "", op_str)
            # str = str
            Namelist.append(op_str)
            Origlist.append(lines[i])
    return Origlist, Namelist


def main():
    # path = "Wikipedia_topics.txt"
    path = "Wikipedia_topics"
    start = 20
    end = 30 #311
    orig, Namelist = FNameparser(path, start, end)
    print(orig)
    print(Namelist)
    executions = len(Namelist)
    i = 0
    correct = 0
    while i < executions:
        print("\n###################################")
        print("Downloading for Topic:", Namelist[i])
        topic = str(Namelist[i])
        topic = topic.strip()
        if retrieve_text(topic):
            print("Download Complete")
            correct += 1
        else:
            print("Skipping the current topic")
            end = end + 1
            _, temp = FNameparser(path, end, end + 1)
            print("Searching another topic")
            Namelist.append(temp[0])
            executions += 1
        i += 1
        print("###################################")
    print("\n Document Searched:", i)
    print("Document Downloaded:", correct)

if __name__ == '__main__':
    main()
