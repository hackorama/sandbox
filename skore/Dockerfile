FROM python:3

ADD requirements.txt requirements.txt
ADD server.py server.py
ADD skore.py skore.py

RUN pip3 install -r requirements.txt

EXPOSE 5000

ENTRYPOINT [ "python3" ]
CMD [ "/server.py" ]
