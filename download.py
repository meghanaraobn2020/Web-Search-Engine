import requests
import re
import os
import random


def retrieve_text(topic):
    # this is the config for to get the first introduction of a title
    data_flag = True
    image_flag = True
    inp_filename = ""
    counter = 0
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

        # print(text_page)
        if text_page['extract'] == '':
            # data_flag = False
            raise Exception("No data available for download")

        print("Starting to write Document...")
        inp_filename = "../Dataset/" + text_page['title'] + ".txt"
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

        # print(image_page)
        filename_pattern = re.compile(".*\.(?:jpe?g|gif|png|JPE?G|GIF|PNG)")


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

            if filename_pattern.search(url_page['imageinfo'][0]['url']) and (counter == 0):
                print("Starting to write Images...")
                counter = counter + 1
                temp = inp_filename.rsplit(".", 1)[0] + "." + (url_page['imageinfo'][0]['url'].rsplit("/", 1)[1]).rsplit(".", 1)[1];
                print("Image File Name: ", temp)
                with open(temp, 'wb') as handle:
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
    if (image_flag == False and data_flag == True) or (data_flag == True and counter == 0):
        if os.path.exists(inp_filename):
            os.remove(inp_filename)
            print("File removed :", inp_filename)
        else:
            print("The file does not exist")
        return False

    if image_flag == False or data_flag == False:
        return False

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
    path = "Wikipedia_topics"
    start = 1000
    end = 25000  # 311
    orig, Namelist = FNameparser(path, start, end)
    print(orig)
    print(Namelist)
    executions = len(Namelist)
    i = 1
    correct = 0
    total_executions = 1000 #Change it to 1000
    systemRandom = random.SystemRandom()
    while correct < total_executions:
        index = systemRandom.randint(0, len(Namelist)-1)
        print("\n###################################")
        print("Execution Number: ", i)
        print("Topics Downloaded: ", correct)
        print("Current Index: ", index)
        print("Downloading for Topic:", Namelist[index])
        topic = str(Namelist[index])
        topic = topic.strip()
        if retrieve_text(topic):
            Namelist.pop(index)
            print("Download Complete")
            correct += 1
        else:
            print("Skipping the current topic")
            end = end + 1
            Namelist.pop(index)
           # _, temp = FNameparser(path, end, end + 1)
           # print("Searching another topic")
            #Namelist.append(temp[0])
            executions += 1
        i += 1

        print("###################################")
    print("\n Document Searched:", i)
    print("Document Downloaded:", correct)
    print("End Index", end)


if __name__ == '__main__':
    main()
