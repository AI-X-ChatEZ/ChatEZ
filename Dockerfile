FROM python:3.10

WORKDIR /app

COPY requirements.txt /app/

RUN apt-get update && apt-get install -y g++ default-jdk
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64

RUN pip install --no-cache-dir --upgrade -r /app/requirements.txt

COPY . /app/

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
